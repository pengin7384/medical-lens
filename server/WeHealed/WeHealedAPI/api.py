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

# test
def test(request):
    text = request.GET.get('text', '')
    print(text)
    #return JsonResponse({'req_data': text})
    return JsonResponse({'text': text})








# Request MachineTranslationRequestJSON 
# Response MachineTranslationResponseJSON

@csrf_exempt
def request_translate(request):
    if request.method == 'POST':
        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        sentences = request_data['sentences']
        
        # 합치기
        
        # 각각의 original문장 앞에 trans_만 붙여서 translated_sentence에 저장(테스트용)
        for sentence in sentences:
            sentence['translated_sentence'] = "trans_"+sentence['original_sentence']
        
        
        describing_url = {
            'key':'좌심실',
            'url':'http'
        }
        
        describing_urls = [ ]
        for i in range(0, 5):
            describing_urls.append(describing_url)
            
   

        responseData = {
            'response_time' : '11:11:11',
            'picture_file_name' : '1.jpg',
            'sentences' : sentences,
            'summary' : 'summarytext!',
            'describing_urls' : describing_urls
        }
        
        print (responseData)
        
        return JsonResponse(responseData)
    else:
        return JsonResponse({'picture_file_name': 'fail'})
    
    


# Google Cloud Language API
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
    tokens = client.analyze_syntax(document).tokens

    # part-of-speech tags from enums.PartOfSpeech.Tag
    pos_tag = ('UNKNOWN', 'ADJ', 'ADP', 'ADV', 'CONJ', 'DET', 'NOUN', 'NUM',
               'PRON', 'PRT', 'PUNCT', 'VERB', 'X', 'AFFIX')

    for token in tokens:
        print(u'{}: {}'.format(pos_tag[token.part_of_speech.tag],
                               token.text.content))
    
    return JsonResponse({'text': text})




def trans(request):
    status = 'init'
    sentence = request.GET.get('sentence', '')
    
    if len(sentence) == 0:
        status = 'Error'
        result = 'Input Sentence'
    else:
        urls = {}
        result = abbriviation(sentence, urls)    # 약어 치환
        print ('urls : ' + str(urls))
        result = rule_substitution(result, urls)    # 룰 치환
        print ('urls : ' + str(urls))
        result = translate_api(result)     # 구글 번역
        urls = find_url(urls)    # url 매핑
        
    
    return JsonResponse({'response_time': '', 'picture_file_name':'', 
                         'sentences':{ 'sentence_number':1, 'original_sentence':sentence, 'translated_sentence':result}, 
                         'summary': 'summary', 'urls':urls})


# 약어 치환 (Dictionary)
def abbriviation(sentence, urls):
    dic = Dictionary.objects
    token_sentence = sentence.split(' ')
    
    result = ''
    for tok in token_sentence:
        try:
            words = dic.filter(abbreviation_text=tok)

            if(len(words)==0):
                result += tok + ' '
            else:
                # print (words)
                for w in words:
                    result += w.original_text
                    urls[w.original_text] = tok
                    break
            # result += word.original_text + ' '
        except Dictionary.DoesNotExist:
            result += tok + ' '
                
    print ('Substitution result : ' + result)            
    print ('urls : ' + str(urls))
    return result


# google translate api
def translate_api(sentence):
    # input = request.GET.get('sentence', '')
    target = 'ko'
    translate_client = translate.Client()

    if isinstance(sentence, six.binary_type):
        sentence = sentence.decode('utf-8')

    # Text can also be a sequence of strings, in which case this method   # will return a sequence of results for each text.
    result = translate_client.translate(sentence, target_language=target)

    print(u'Text: {}'.format(result['input']))
    print(u'Translation: {}'.format(result['translatedText']))
    print(u'Detected source language: {}'.format(result['detectedSourceLanguage']))
    return result

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
                    print(row)
                    urls[key] = row
        except Exception as e:
            print ('Error occured!!! ' + e)
            continue
        
    print ('[find_url] urls : ' + str(urls))
    conn.close()    
    return urls



def pick_importants(request):
    from WeHealedAPI.Interpreter.negex import *
    rfile = open(r'Interpreter/negex_triggers.txt')
    irules = sortRules(rfile.readlines())
    
    sentences = request.GET.get('sentences', '')
    for sentence in sentences:
        query = sentence.original_sentence
    
    return 0


# 미 완 성
def get_date(request):
    import re
    # reg = "\b([0-9]{1,2} ?([\\-/\\\\] ?[0-9]{1,2} ?| (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) ?)([\\-/\\\\]? ?('?[0-9]{2}|[0-9]{4}))?)\b?"
    sentence = request.GET.get('sentence', '')
    # searched = re.search(reg, sentence)
    searched = re.search("\b([0-9]{1,2} ?([\\-/\\\\] ?[0-9]{1,2} ?| (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) ?)([\\-/\\\\]? ?('?[0-9]{2}|[0-9]{4}))?)\b?", sentence)
    if searched == None:
        return JsonResponse({'status':'Error'})
    date = searched.group(1)
    return JsonResponse({'status':'donno', 'result':date})


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


# rule test

'''
def rule(request):
    #text = request.GET.get('text', '')
    pattern = r'Lymph node. "(?P<num>.+)"'
    text = request.GET.get('text', '')

    r = re.compile(pattern, re.IGNORECASE)
    m = r.search(text)
    if(m):
        newText = r.sub('"' + m.group("num") + '번 림프절"', text)
    else:
        newText = text
    print(newText)
 
    return JsonResponse({'text': newText})
'''


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











