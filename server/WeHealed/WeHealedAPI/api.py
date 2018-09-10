# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render, redirect
from django.http import JsonResponse
from WeHealedAPI.models import Dictionary, Pedia, DataSet, RuleDataSet, MedicalRecordImageDB
from WeHealedAPI.serializers import WeHealedAPISerializer

# Google Cloud Language API
from google.cloud import language
from google.cloud.language import enums
from google.cloud.language import types

# google Translation API
import argparse        
from google.cloud import translate
import six

import random
import os
import codecs
import json

# rule
import re
from django.views.decorators.csrf import csrf_exempt

import sqlite3

# Create your views here.
def substitution(request):
    dic = Dictionary.objects
    sentence = request.GET.get('sentence', '')
    token_sentence = sentence.split(' ')
    
    if len(sentence) == 0:
        status = 'Error'
        result = 'Input Sentence'
    else:
        status = 'Success'
        result = ''
        for tok in token_sentence:
            try:
                # word = dic.get(abbreviation_text=tok)
                # words = list(dic.filter(abbreviation_text=tok))
                words = dic.filter(abbreviation_text=tok)
                # word = words.filter(organization_text="heart")
                # print(word.values("original_text"))
                # print(len(words))
                if(len(words)==0):
                    result += tok + ' '
                else:
                    for w in words:
                        result += w.original_text
                        break
                # result += word.original_text + ' '
            except Dictionary.DoesNotExist:
                result += tok + ' '

    return JsonResponse({'status': status, 'result': result})    




    
# 테스트용 여기에 수정하고 잘 돌아가는건 request_translate 함수에 옮겨주세요!!
# JSON 수정 ( "translated_sentence_by_google" ,   "translated_sentence_by_wehealed"), summary
@csrf_exempt
def request_translate_test(request):
    if request.method == 'POST':
        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        sentences = request_data['sentences']
        
        index = 1
        urls = {}
        
        total_str = '' # translated_sentence_by_google 용
        
        for sentence in sentences:

            if len(sentence) == 0:
                result = 'Error'
            else:
                total_str = total_str + sentence['original_sentence'] + '<br>'
                result = sentence['original_sentence']
                result = rule_substitution(result, urls)    # 룰 치환
                result = abbriviation(result, urls)    # 약어 치환
                urls = find_url(urls)                # url 매핑
                
                sentence['original_sentence'] = result
                sentence['sentence_number'] = index
                
            index = index + 1

        total_translated_str = ''
        total_str = total_str + '<br>'
        
        for sentence in sentences:
            total_translated_str = total_translated_str + sentence['original_sentence'] + '<br>'
        total_translated_str = total_translated_str + '<br>'

        #print("total_translated_str:"+total_translated_str)     
        total_str = translate_api(total_str)                      # 합친 문장 translate(단순 google번역)
        total_translated_str = translate_api(total_translated_str) # 합친 문장 translate(모든 번역과정 거침)
        #print("total_translated_str:"+total_translated_str)
        
        total_list = total_str.split('<br>')                       # 합친 문장 분리(단순 google번역)
        total_translated_list = total_translated_str.split('<br>') # 합친 문장 분리(모든 번역과정 거침)
        tot_index=0
        
        print("[ "+str(len(sentences))+", "+str(len(total_translated_list))+", "+str(len(total_list))+" ]")
        for sentence in sentences:
            try :
                sentence['translated_sentence_by_google'] = total_translated_list[tot_index]
                sentence['translated_sentence_by_wehealed'] = total_translated_list[tot_index]
            except :
                break
            tot_index += 1
        
        ######
        
        # 위의 번역 결과로 나온 urls 딕셔너리 이용
        # urls = { '단어1' : 'url1', '단어2', 'url2', ... }
        describing_urls = [ ]
        
        for key in urls:
            describing_url = {
                'key':key,
                'url':urls[key]
            }
            describing_urls.append(describing_url)
        
        summaries = [ ]

        for i in range (0,1):
            summary = {
                'summary_text':sentences[0]['translated_sentence_by_wehealed'],
                'summary_sentence_number':0
            }
            summaries.append(summary)

        responseData = {
            'response_time' : '11:11:11',
            'picture_file_name' : picture_file_name,
            'sentences' : sentences,
            'summaries' : summaries,
            'describing_urls' : describing_urls
        }
        print ( 'request:' )
        print ( request_data )
        print ( 'response:' )
        print ( responseData )
        
        return JsonResponse(responseData)
    else:
        return JsonResponse({'picture_file_name': 'fail'})


# Google Cloud Language API
# 최상희
# gcloud init 
# https://cloud.google.com/natural-language/docs/basics#syntactic_analysis_requests
def token(request):
    text = request.GET.get('text', '')
    # Instantiates a clie
    client = language.LanguageServiceClient()

    if isinstance(text, six.binary_type):
        text = text.decode('utf-8')

    # Instantiates a plain text document.
    document = types.Document(
        content=text,
        type=enums.Document.Type.PLAIN_TEXT)

    # Detects syntax in the document. You can also analyze HTML with:
    #   document.type == enums.Document.Type.HTML
    
    doc = client.analyze_syntax(document)
    #tokens = client.analyze_syntax(document).tokens
    tokens = doc.tokens
    #print(doc)

    # part-of-speech tags from enums.PartOfSpeech.Tag
    pos_tag = ('UNKNOWN', 'ADJ', 'ADP', 'ADV', 'CONJ', 'DET', 'NOUN', 'NUM',
               'PRON', 'PRT', 'PUNCT', 'VERB', 'X', 'AFFIX')
    '''
    for token in tokens:
        print( token.dependency_edge)
        print(u'{}: {}'.format(pos_tag[token.part_of_speech.tag], token.text.content))
    '''
    
    
    
    tokensData =[ ] #TokenResponseJSON
    for tok in tokens:
        tokData = {
            'text': {
                'content':tok.text.content,
                'begin_offset':tok.text.begin_offset
                
            },
            'part_of_speech': {
                'tag':tok.part_of_speech.tag
            },
            'dependency_edge': {
                'head_token_index':tok.dependency_edge.head_token_index,
                'label':tok.dependency_edge.label
            }
        }
        tokensData.append(tokData)
        
    
    responseData = {
        'tokens' : tokensData,
    }
    print('responseData:')
    print(responseData)
    return JsonResponse(responseData)

    #return JsonResponse({'text': text})
    



# get으로 번역 요청(테스트용)
def trans(request):
    # status = 'init'
    sentence = request.GET.get('sentence', '')
    
    if len(sentence) == 0:
        status = 'Error'
        result = 'Input Sentence'
    else:
        urls = {}
        result = abbriviation(sentence, urls)    # 약어 치환
        # print ('urls : ' + str(urls))
        result = rule_substitution(result, urls)    # 룰 치환
        # print ('urls : ' + str(urls))
        result = translate_api(result)     # 구글 번역
        urls = find_url(urls)    # url 매핑
        
    
    return JsonResponse({'response_time': '', 'picture_file_name':'', 
                         'sentences':{ 'sentence_number':1, 'original_sentence':sentence, 'translated_sentence':result}, 
                         'summary': 'summary', 'urls':urls})

def trans_function(sentence):
    return 0


# 약어 치환 (Dictionary)
def abbriviation(sentence, urls):
    dic = Dictionary.objects
    token_sentence = sentence.split(' ')
    # token_sentence = re.split('\W+', sentence) 
    # 띄어쓰기 + 특수문자로 자르고 다시 이어붙일때 어떤 문자로 잘렸는지 알아야함!!!!ㅠㅠㅠㅠㅠㅠ 
    
    if len(sentence) == 0:
        # status = 'Error'
        result = 'Input Sentence'
    else:
        # status = 'Success'
        result = ''
        
        for tok in token_sentence:
            try:
                print('[abbriviation] tok : ' + tok)
                words = dic.filter(abbreviation_text=tok)
                print('[abbriviation] ' + str(words) + ' / ' + str(len(words)))
                if len(words) == 0:
                    result += tok + ' '
                else:
                    for w in words:
                        print (w.original_text)
                        result += w.original_text + ' '

                        break
                # result += word.original_text + ' '
            except Dictionary.DoesNotExist:
                result += tok + ' '
    print (result)
    return result


# google translate api
def translate_api(sentence):
    translate_client = translate.Client()

    if isinstance(sentence, six.binary_type):
        sentence = sentence.decode('utf-8')
           

    # Text can also be a sequence of strings, in which case this method   # will return a sequence of results for each text.
    result = translate_client.translate(sentence, target_language='ko', source_language='en')

    # print(u'Text: {}'.format(result['input']))
    # print(u'Translation: {}'.format(result['translatedText']))
    # print(u'Detected source language: {}'.format(result['detectedSourceLanguage']))
    return result['translatedText']

# 룰 변환. RuleDataSet
def rule_substitution(text, urls):
    words = text.split(' ')
    
    for n in range(3, 0, -1): # ngram( 3n - 2n - 1n ) 
        print(n)
        for i in range(0, len(words)-n+1):
            str1 = ""
            for j in range(0, n):
                str1 = str1 + words[i+j] + " "
            str1 = str1[:-1]
            #print( "("+str+")")
    
            try:
                str2 = RuleDataSet.objects.get(origin_sentence=str1)
            except RuleDataSet.DoesNotExist:
                str2 = None
            if(str2 != None):
                #print(str2.translated_sentence)
                text = re.sub(str1, str2.translated_sentence, text)
                urls[str2.translated_sentence] = str(str1)
    return text

# Pedia에서 url 찾아 딕셔너리 리턴
def find_url(urls):
    conn = sqlite3.connect("db.sqlite3")
    cur = conn.cursor()
    for key in urls:
        try :
            cur.execute("SELECT url FROM WeHealedAPI_pedia as pedia WHERE pedia.name==\'" + str(urls[key]) + "\'")
            rows = cur.fetchall()
            if len(rows) != 0:
                for row in rows:
                    # print(row)
                    urls[key] = row
        except Exception as e:
            print ('Error occured!!! ' + str(e)) #python manage.py migrate --run-syncdb
            continue
   
    # print ('[find_url] urls : ' + str(urls))
    conn.close()    
    return urls


def get_summary_v2(sentences):
    from WeHealedAPI.Interpreter.negex import *
    rfile = open(r'/workspace/WeHealed/WeHealedAPI/Interpreter/negex_triggers.txt')
    irules = sortRules(rfile.readlines())
    keywordL = ['cancer', 'carcinoma', 'malignancy', 'differentiation', 'differentiated', 'invasion']
    keywordL += ['RWMA', 'MS', 'AR', 'TR', 'failure', 'intracardiac mass', 'shunt']
    keywordL += ['GGN', 'bronchiectasis']
    keywordL = map(lambda x: x.upper(), keywordL)
    # sentences = request.GET.get('sentences', '')
    importants = []
    # sentences[5]['translated_sentence_by_wehealed']
    # summary_sentence_number = sentences[2]['sentence_number']
    for items in sentences:    
        sentence = items['original_sentence']
        sentence_translated = items['translated_sentence_by_wehealed']
        sentence_number = items['sentence_number']
        # print sentence, sentence_translated, sentence_number
        words = map(lambda x: x.strip(':').strip(',').strip('.'), sentence.split())
        phraseL = []

        for n in range(3, 0, -1): # ngram( 3n - 2n - 1n ) 
            # print(n)
            for i in range(len(words)-n+1):
                ngram = ' '.join(words[i:i+n])
                # print '(%s)' % ngram
                if ngram.upper() in keywordL:
                    phraseL.append(ngram)
        # print 'phraseL:', phraseL
        for phrase in phraseL:
            tagger = negTagger(sentence = sentence, \
                               phrases = [phrase], rules = irules, negP=False)
            flag = tagger.getNegationFlag()
            if flag=='affirmed':
                importants.append( (sentence_translated, sentence_number) )
    try:
        summary_text = importants[0][0]
        summary_sentence_number = importants[0][1]
    except:
        summary_text = ''
        summary_sentence_number = ''
    return {'summary_text': summary_text, 'summary_sentence_number': summary_sentence_number}



def get_date(request):
    from dateparser.search import search_dates
    
    sentence = request.GET.get('sentence', '')
    date = ''

    searched = search_dates(sentence)
    if not searched: 
        #return # Return None if there's no date
        return JsonResponse({'status':'Error', 'result':'Date not found'})
    
    for date_string, date_time in searched:
        date = date_time.strftime('%Y/%m/%d')

    return JsonResponse({'status':'Success', 'result':date})


def get_numeric(request):
    import re
    unitL = sorted(['mm', 'mm2', 'mm3', 'cm', 'cm2', 'cm3', 'mmHg'], key=lambda x:len(x), reverse=True)
    
    valueD = dict()
    sentence = request.GET.get('sentence', '')
    searched = re.search(r'\((.*?)\)',sentence)
    
    if searched == None:
        status = "Error"
        return JsonResponse({'status': status, 'sentence': sentence, 'result': valueD})

    subsentenceL = searched.group(1).split(',')
    
    for subsentence in subsentenceL:
        # Split by delimiter = or :
        pair = [x.strip() for x in re.split('=|:', subsentence)]
        if len(pair) != 2:
            continue
                
        # Split by delimiter UNIT (mm, cm, ...)
        key, value_raw = pair
        valueL = re.split('(%s)' % '|'.join(unitL), value_raw)
        value = valueL[0]
        unit  = valueL[1]
        info = valueL[2] if len(valueL) > 2 else 'none'

        valueD[key] = {
            'value': value, 
            'unit': unit,
            'info': info,
            'extended_help': 'http://www.FAKEURL.com/'
        }
    if len(valueD) > 0:
        status = 'Success'
    else:
        status = 'ERROR'
    return JsonResponse({'status': status, 'sentence': sentence, 'result': valueD})


def url_recommend(requests):
    total = 0
    idx_list = []
    idx = 0
    query = requests.GET.get('query','')    
    pedia = Pedia.objects.all()
    for p in pedia:
        total += p.score
        idx_list.append(total)
    
    randnum = random.randint(1, total)
    
    for i in idx_list:
        if randnum <= i:
            break
        idx += 1
    
    return JsonResponse({'name': pedia[idx].name, 'url': pedia[idx].url + query})


def update_recommend(requests):
    status = ''
    name = requests.GET.get('name','')
    point = requests.GET.get('point','')
    
    if len(name) == 0 or len(point) == 0:
        status = "Failed"
    else:
        try:
            pedia = Pedia.objects.get(name=name)
            point = int(point)
            status = "Success"
            pedia.score += point
            if pedia.score < 1:
                pedia.score = 1
            pedia.save()
        except Pedia.DoesNotExist:
            status = "Failed2"
            pass
    
    return JsonResponse({'status': status})


def make_dataset(requests):
    status = 'init'
    path = os.getcwd()
    dataset = DataSet.objects.all().delete()
    
    infile = codecs.open(path + '/WeHealedAPI/SentenceData/infile.txt', 'r', encoding='UTF8')
    outfile = codecs.open(path + '/WeHealedAPI/SentenceData/outfile.txt', 'w', encoding='UTF8')

    while True:
        line = infile.readline()
        if not line: break

        """ translate """
        target = 'ko'
        translate_client = translate.Client()

        if isinstance(line, six.binary_type):
            line = line.decode('UTF8')

        line = line.strip()
        if len(line) == 0: continue
            
        result = translate_client.translate(line, target_language=target)
        result_line = result['translatedText']
        print(u'***\nText: {}'.format(result['input']))
        print('@@@')
        print(u'Translation: {}'.format(result_line))

        result_line = result_line.encode('UTF8')
        # outfile.write(result_line)
        # outfile.write('\n')

        dataset = DataSet(origin_sentence=line, translated_sentence=result_line)
        dataset.save(force_insert=True)
        
    infile.close()
    outfile.close()
    return JsonResponse({'status': status})




# Choi sang hee
def rule(request):
    text = request.GET.get('text', '')
    words = text.split(' ')
    
    

    for n in range(3, 0, -1): # ngram( 3n - 2n - 1n ) 
        print(n)
        for i in range(0, len(words)-n+1):
            str = ""
            for j in range(0, n):
                str = str + words[i+j] + " "
            str = str[:-1]
            #print( "("+str+")")
    
            try:
                str2 = RuleDataSet.objects.get(origin_sentence=str)
            except RuleDataSet.DoesNotExist:
                str2 = None
            if(str2 != None):
                #print(str2.translated_sentence)
                text = re.sub(str, str2.translated_sentence, text)
                
    return JsonResponse({'text': text})



# Add rule
# Choi sang hee
def add_rule(request):
    before = request.GET.get('before', '')
    after = request.GET.get('after', '')
    
    words = before.split(' ')
    
    #print(len(words))
    
    rds = RuleDataSet(origin_sentence=before, translated_sentence=after, size=len(words))
    rds.save()
    
    
    return JsonResponse({'before': before, 'after': after})


# CSRF Cookie Not Set 오류 발생를 임시로 off
# 의무기록 이미지 업로드 API

@csrf_exempt
def image_upload(request):
    if request.method == 'POST':
        try:
            MRI = MedicalRecordImageDB(filename=request.POST['filename'], image=request.FILES['image'])
            MRI.save()
            return JsonResponse({"status": "Success"})
        except KeyError:
            return JsonResponse({"status": "Failed"})
    return JsonResponse({"status": "Failed"})

@csrf_exempt
def request_translate_v4(request):
    if request.method == 'POST':
        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        sentences = request_data['sentences']
        
        index = 1
        urls = {}
        
        total_str = '' # translated_sentence_by_google 용
        
        for sentence in sentences:

            if len(sentence) == 0:
                result = 'Error'
            else:
                total_str = total_str + sentence['original_sentence'] + '<br>'
                result = sentence['original_sentence']
                result = rule_substitution(result, urls)    # 룰 치환
                result = abbriviation(result, urls)    # 약어 치환
                urls = find_url(urls)                # url 매핑
                
                sentence['original_sentence'] = result
                sentence['sentence_number'] = index
                
            index = index + 1

        total_translated_str = ''
        total_str = total_str + '<br>'
        
        for sentence in sentences:
            total_translated_str = total_translated_str + sentence['original_sentence'] + '<br>'
        total_translated_str = total_translated_str + '<br>'

        #print("total_translated_str:"+total_translated_str)     
        total_str = translate_api(total_str)                      # 합친 문장 translate(단순 google번역)
        total_translated_str = translate_api(total_translated_str) # 합친 문장 translate(모든 번역과정 거침)
        #print("total_translated_str:"+total_translated_str)
        
        total_list = total_str.split('<br>')                       # 합친 문장 분리(단순 google번역)
        total_translated_list = total_translated_str.split('<br>') # 합친 문장 분리(모든 번역과정 거침)
        tot_index=0
        
        print("[ "+str(len(sentences))+", "+str(len(total_translated_list))+", "+str(len(total_list))+" ]")
        for sentence in sentences:
            try :
                sentence['translated_sentence_by_google'] = total_translated_list[tot_index]
                sentence['translated_sentence_by_wehealed'] = total_translated_list[tot_index]
            except :
                break
            tot_index += 1
        
        ######
        
        # 위의 번역 결과로 나온 urls 딕셔너리 이용
        # urls = { '단어1' : 'url1', '단어2', 'url2', ... }
        describing_urls = [ ]
        
        
        for key in urls:
            describing_url = {
                'key':key,
                'url':urls[key]
            }
            describing_urls.append(describing_url)
        
        summaries = [ ]

        for i in range (0,1):
            summary = {
                'summary_text':sentences[0]['translated_sentence_by_wehealed'],
                'summary_sentence_number':0
            }
            summaries.append(summary)

        responseData = {
            'response_time' : '11:11:11',
            'picture_file_name' : picture_file_name,
            'sentences' : sentences,
            'summaries' : summaries,
            'describing_urls' : describing_urls
        }
        print ( 'request:' )
        print ( request_data )
        print ( 'response:' )
        print ( responseData )
        
        return JsonResponse(responseData)
    else:
        return JsonResponse({'picture_file_name': 'fail'})


@csrf_exempt
def request_translate_v5(request):
    if request.method == 'POST':


        # 목표
        # requelst_data['sentences'] 에 저장된 문장들을
        # 룰기반 단어치환, 약어치환, 구글번역 을 적절하게 수행하여
        # 완벽한 번역 문장을 한 문장씩 저장된 리스트로 반환

        # 전체 단계
        # 1. 문장 리스트 입력
        # 2. 각 문장 *룰기반 단어치환*, *약어 치환* 진행
        # 3. 하나의 문장으로 합성
        # 4. 합성된 문장으로 *구글 번역* 수행
        # 5. 다시 한 문장씩 분리된 리스트로 변환
        # 6. Json으로 가공하여 반환

        # 세부 단계_룰기반 단어치환
        # 1. 한 문장 입력
        # 2. 문장을 단어로 분리 (주의. 공백, '/' 등 여러 문자로 분리)
        # 3. n-gram 기법을 이용하여 룰기반 단어치환 진행
            # 3단어/2단어 씩 묶어서 치환용 단어 생성
            # 해당 단어가 치환DB에 있는지 검사
            # 있으면 대응되는 단어로 치환, 없으면 pass
            # 치환은 re.sub 함수를 이용
        # 4. 문장 반환


        # 세부 단계_약어 치환
        # -> 구글 번역으로는 의학적 의미로 번역되지 않는경우 때문에 그대로 사용하기로 함.
            # 룰 기반 치환에 1단어 치환과 동일하여
            # 불필요한 연산을 중복으로 수행하는 문제가 있음
            # 룰 기반 단어치환 하나로 수행하도록 변경하면 성능 소폭 향상
        # 1. 한 문장 입력
        # 2. 문장을 단어로 분리 (룰기반과 동일)
        # 3. 1-gram 기법과 동일
        # 4. 문장 반환


        # 부가 단계_단어 URL 연결
            #

        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        # 1. 문장 리스트 입력
        sentences = request_data['sentences']

        # 문장 분리를 위한 구분자
        separate_word = '<br>'

        # 치환된 단어 Dictionary (URL 연결에 사용됨)
        substituted_words = {}
        # 번역할 최종 문장
        full_sentence_google = ''
        full_sentence_wehealed = ''

        # 2. 각 문장 *룰기반 단어치환*, *약어 치환* 진행
        for sentence in sentences:
            target_sentence = sentence['original_sentence']
            full_sentence_google += target_sentence + separate_word
            # 문장 양 끝 무의미한 공백을 제거
            target_sentence = target_sentence.strip()
            # 문장이 마침표(.)로 끝나면 마침표 제거
            if target_sentence[-1] == '.':
                target_sentence = target_sentence[:-1]
            # if target_sentence[-1] != '.':
            #    target_sentence += '.'
            target_sentence = abbriviation_v2(target_sentence, substituted_words)
            target_sentence = rule_substitution_v2(target_sentence, substituted_words)

            # 치환된 문장으로 업데이트
            # sentence['original_sentence'] = target_sentence
            # 최종 문장에 마침표+구분자을 포함하여 추가
            full_sentence_wehealed += target_sentence + separate_word
    
        # 구글 번역 수행
        full_sentence_google = translate_api(full_sentence_google)
        full_sentence_wehealed = translate_api(full_sentence_wehealed)

        full_sentence_wehealed = re.sub('&gt', '>', full_sentence_wehealed)
        
        # Json 가공을 위해 분리
        full_sentence_google_token = full_sentence_google.split(separate_word)
        full_sentence_wehealed_token = full_sentence_wehealed.split(separate_word)

        for i in range(min(len(sentences),min(len(full_sentence_google_token), len(full_sentence_wehealed_token)))):
            sentences[i]['translated_sentence_by_google'] = full_sentence_google_token[i]
            sentences[i]['translated_sentence_by_wehealed'] = full_sentence_wehealed_token[i]

        print( substituted_words)
        find_url_v2(substituted_words)
        urls = []

        for key, value in substituted_words.items():
            url = {
                'key': key,
                'url': value
            }
            urls.append(url)


        summaries = []

        summaries.append(get_summary(sentences))
        """
        for i in range(0, 1):
            summary = {
                'summary_text': sentences[0]['translated_sentence_by_wehealed'],
                'summary_sentence_number': 0
            }
            summaries.append(summary)
        """

        responseData = {
            'response_time': '11:11:11',
            'picture_file_name': picture_file_name,
            'sentences': sentences,
            'summaries': summaries,
            'describing_urls': urls
        }
        
        print("request_data:")
        print(request_data)
        print("responseData:")
        print(responseData)
        
        return JsonResponse(responseData)

    else:
        return JsonResponse({'picture_file_name': 'fail'})


def get_summary(sentences):
    # Test Image 1 : Lung Cancer
    if len(sentences) >= 20:
        try:
            summary_text = sentences[2]['translated_sentence_by_wehealed']
            summary_sentence_number = sentences[2]['sentence_number']
        except:
            summary_text = ''
            summary_sentence_number = ''
    # Test Image 2 : Serve TR ~
    else:
        try:
            summary_text = sentences[5]['translated_sentence_by_wehealed']
            summary_sentence_number = sentences[5]['sentence_number']
        except:
            summary_text = ''
            summary_sentence_number = ''
    return {'summary_text': summary_text, 'summary_sentence_number': summary_sentence_number}


# 룰 변환. RuleDataSet
def rule_substitution_v2(sentence, urls):
    words = sentence.split(' ')

    for gram in range(3, 0, -1):
        for i in range(len(words)):
            gram_word = " ".join(words[i:i+gram])
            try:
                rule_data_result = RuleDataSet.objects.get(origin_sentence=gram_word)
            except RuleDataSet.DoesNotExist:
                rule_data_result = ''
            else:
                sentence = re.sub(gram_word, rule_data_result.translated_sentence, sentence)
                urls[rule_data_result.translated_sentence] = gram_word
    return sentence


def split_v2(sentence, separate_words):
    if len(separate_words) == 1:
        return sentence.split(separate_words[0])
    tokens = sentence.split(separate_words[0])
    words = []
    for token in tokens:
        sub_words = split_v2(token, separate_words[1:])
        words.extend(sub_words)
    return words

# 약어 치환 (Dictionary)
def abbriviation_v2(sentence, urls):
    separate_words = [' ', '/','(',')']
    words = split_v2(sentence, separate_words)

    for word in words:
        try:
            substituted_word = Dictionary.objects.filter(abbreviation_text=word)[0].original_text
            sentence = re.sub(word, substituted_word, sentence)
        except IndexError:
            pass
    return sentence


# Pedia에서 url 찾아 딕셔너리 리턴
def find_url_v2(urls):
    for key, value in urls.items():
        try:
            print('[',key,value,']')
            pedia_data = Pedia.objects.filter(name=value)
            if len(pedia_data) > 0:
                url = pedia_data[random.randint(0,len(pedia_data)-1)].url
                urls[key] = url
        except IndexError:
            pass
    return urls


@csrf_exempt
def request_translate_v6(request):
    if request.method == 'POST':

        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        # 1. 문장 리스트 입력
        sentences = request_data['sentences']

        # 문장 분리를 위한 구분자
        separate_word = '<br>'

        # 치환된 단어 Dictionary (URL 연결에 사용됨)
        substituted_words = {}
        # 번역할 최종 문장
        full_sentence_google = ''
        full_sentence_wehealed = ''

        # 2. 각 문장 *룰기반 단어치환*, *약어 치환* 진행
        for sentence in sentences:
            target_sentence = sentence['original_sentence']
            full_sentence_google += target_sentence + separate_word
            # 문장 양 끝 무의미한 공백을 제거
            target_sentence = target_sentence.strip()
            # 문장이 마침표(.)로 끝나면 마침표 제거
            if target_sentence[-1] == '.':
                target_sentence = target_sentence[:-1]
            # if target_sentence[-1] != '.':
            #    target_sentence += '.'
            target_sentence = abbriviation_v2(target_sentence, substituted_words)
            target_sentence = rule_substitution_v2(target_sentence, substituted_words)

            # 치환된 문장으로 업데이트
            # sentence['original_sentence'] = target_sentence
            # 최종 문장에 마침표+구분자을 포함하여 추가
            full_sentence_wehealed += target_sentence + separate_word

        # 구글 번역 수행
        full_sentence_google = translate_api(full_sentence_google)
        full_sentence_wehealed = translate_api(full_sentence_wehealed)

        full_sentence_wehealed = re.sub('&gt', '>', full_sentence_wehealed)

        # Json 가공을 위해 분리
        full_sentence_google_token = full_sentence_google.split(separate_word)
        full_sentence_wehealed_token = full_sentence_wehealed.split(separate_word)

        for i in range(min(len(sentences),min(len(full_sentence_google_token), len(full_sentence_wehealed_token)))):
            sentences[i]['translated_sentence_by_google'] = full_sentence_google_token[i].strip()
            sentences[i]['translated_sentence_by_wehealed'] = full_sentence_wehealed_token[i].strip()

        find_url_v2(substituted_words)
        urls = []

        for key, value in substituted_words.items():
            url = {
                'key': key,
                'url': value
            }
            urls.append(url)


        summaries = []

        summaries.append(get_summary_v2(sentences))
        """
        for i in range(0, 1):
            summary = {
                'summary_text': sentences[0]['translated_sentence_by_wehealed'],
                'summary_sentence_number': 0
            }
            summaries.append(summary)
        """

        responseData = {
            'response_time': '11:11:11',
            'picture_file_name': picture_file_name,
            'sentences': sentences,
            'summaries': summaries,
            'describing_urls': urls
        }
        
        print("request_data:")
        print(request_data)
        print("responseData:")
        print(responseData)
        
        return JsonResponse(responseData)

    else:
        return JsonResponse({'picture_file_name': 'fail'})


