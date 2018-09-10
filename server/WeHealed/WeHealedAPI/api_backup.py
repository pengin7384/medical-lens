# api.py에서 테스트용이거나 안쓰이는 함수들 백업 파일



# test
def test(request):
    text = request.GET.get('text', '')
    print(text)
    #return JsonResponse({'req_data': text})
    return JsonResponse({'text': text})




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



# Request MachineTranslationRequestJSON 
# Response MachineTranslationResponseJSON

# POST
@csrf_exempt
def request_translate(request):
    if request.method == 'POST':
        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        sentences = request_data['sentences']
        
        # print( request_data )
        
        index = 1
        urls = {}
        for sentence in sentences:
            # sentence['translated_sentence'] = "trans_"+sentence['original_sentence'] # 테스트용
            
            if len(sentence) == 0:
                result = 'Error'
            else:
                result = sentence['original_sentence']
                result = rule_substitution(result, urls)    # 룰 치환
                result = abbriviation(result, urls)    # 약어 치환
                #result = translate_api(result)         # 구글 번역
                urls = find_url(urls)                # url 매핑
                
                sentence['translated_sentence'] = result
                sentence['sentence_number'] = index
                # result_group = { 'sentence_number':index,
                #                  'original_sentence':sentence,
                #                  'translated_sentence':result
                #                }
                
            index = index + 1

        
        #######
        total_str = ''
        
        for sentence in sentences:
            total_str = total_str + sentence['original_sentence'] + ' ... '
        total_str = total_str + ' ... '
        '''
        
        for sentence in sentences:
            sentence['original_sentence'] = total_str
            sentence['translated_sentence'] = translate_api(total_str)
            break
        '''
        
        #total_str = translate_api(total_str)
        
        total_list = total_str.split(' ... ')
        tot_index=0
        print("[ "+str(len(sentences))+", "+str(len(total_list))+" ]")
        for sentence in sentences:
            sentence['translated_sentence'] = total_list[tot_index]
            '''
            try :
                sentence['translated_sentence'] = total_list[tot_index]
            except :
                break
            '''
                
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
        


        responseData = {
            'response_time' : '11:11:11',
            'picture_file_name' : picture_file_name,
            'sentences' : sentences,
            'summary' : 'summarytext!',
            'describing_urls' : describing_urls
        }
        
        print (responseData)
        
        return JsonResponse(responseData)
    else:
        return JsonResponse({'picture_file_name': 'fail'})
    
    
# POST
@csrf_exempt
def request_translate_v2(request):
    if request.method == 'POST':
        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        sentences = request_data['sentences']
        
        index = 1
        urls = {}
                                                    
                                                    
        for sentence in sentences:
            if len(sentence) == 0:
                result = 'Error'
            else:
                result = sentence['original_sentence']
                result = rule_substitution(result, urls)    # 룰 치환
                result = abbriviation_v2(result, urls)    # 약어 치환
                #result = translate_api(result)         # 구글 번역
                urls = find_url(urls)                # url 매핑
                
                sentence['translated_sentence'] = result
                sentence['sentence_number'] = index
                # result_group = { 'sentence_number':index,
                #                  'original_sentence':sentence,
                #                  'translated_sentence':result
                #                }
                
            index = index + 1
        # print ('==========================')
        # print (sentences)
        # print ('"')
        # print ('"')
        
        #######
        total_str = ''
        
        for sentence in sentences:
            if sentence['original_sentence'][-1] == '.':
                total_str += sentence['original_sentence'] + ' '
            else:
                total_str += sentence['original_sentence'] + '. '
        '''
        
        for sentence in sentences:
            sentence['original_sentence'] = total_str
            sentence['translated_sentence'] = translate_api(total_str)
            break
        '''
        
        total_str = translate_api(total_str)
        
        total_list = total_str.split('. ')
        tot_index=0
        print("[ "+str(len(sentences))+", "+str(len(total_list))+" ]")
        for sentence in sentences:
            try:
                sentence['translated_sentence'] = total_list[tot_index]
                tot_index += 1
            except:
                break
        
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
        


        responseData = {
            'response_time' : '11:11:11',
            'picture_file_name' : picture_file_name,
            'sentences' : sentences,
            'summary' : 'summarytext!',
            'describing_urls' : describing_urls
        }
        
        # print (responseData)
        
        return JsonResponse(responseData)
    else:
        return JsonResponse({'picture_file_name': 'fail'})
    

# 약어 치환 (Dictionary)
def abbriviation_v2(sentence, urls):
    dic = Dictionary.objects
    token_sentence = sentence.split(' ')
    
    if len(sentence) == 0:
        # status = 'Error'
        result = 'Input Sentence'
    else:
        # status = 'Success'
        result = ''
        
        for tok in token_sentence:
            try:
                # print('[abbriviation] tok : ' + tok)
                # word = dic.get(abbreviation_text=tok)
                # words = list(dic.filter(abbreviation_text=tok))
                words = dic.filter(abbreviation_text=tok)
                # word = words.filter(organization_text="heart")
                # print(word.values("original_text"))
                # print('[abbriviation] ' + str(words) + ' / ' + str(len(words)))
                if len(words) == 0:
                    result += tok + ' '
                else:
                    for w in words:
                        # print (w.original_text)
                        result += w.original_text + ' '

                        break
                # result += word.original_text + ' '
            except Dictionary.DoesNotExist:
                result += tok + ' '
    # print (result)
    return result




@csrf_exempt
def request_translate_v3(request):
    if request.method == 'POST':
        request_data = ((request.body).decode('utf-8'))
        request_data = json.loads(request_data)
        picture_file_name = request_data['picture_file_name']
        sentences = request_data['sentences']

        index = 1
        urls = {}
        for sentence in sentences:

            if len(sentence) == 0:
                result = 'Error'
            else:
                result = sentence['original_sentence']
                result = rule_substitution(result, urls)    # 룰 치환
                result = abbriviation(result, urls)    # 약어 치환
                urls = find_url(urls)                # url 매핑
                
                sentence['original_sentence'] = result
                sentence['sentence_number'] = index
                
            index = index + 1

        total_str = ''
        
        for sentence in sentences:
            total_str = total_str + sentence['original_sentence'] + '<br>'
        total_str = total_str + '<br>'
        '''
        
        for sentence in sentences:
            sentence['original_sentence'] = total_str
            sentence['translated_sentence'] = translate_api(total_str)
            break
        '''
        print("total_str:"+total_str)
        total_str = translate_api(total_str)
        print("total_str:"+total_str)
        
        total_list = total_str.split('<br>')
        tot_index=0
        print("[ "+str(len(sentences))+", "+str(len(total_list))+" ]")
        for sentence in sentences:
            try :
                sentence['translated_sentence'] = total_list[tot_index]
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
        


        responseData = {
            'response_time' : '11:11:11',
            'picture_file_name' : picture_file_name,
            'sentences' : sentences,
            'summary' : 'summarytext!',
            'describing_urls' : describing_urls
        }
        
        print (responseData)
        
        return JsonResponse(responseData)
    else:
        return JsonResponse({'picture_file_name': 'fail'})
    
    
'''
v4 : JSON 변경(+구글+summaries)
'''
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