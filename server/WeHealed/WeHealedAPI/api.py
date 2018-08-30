# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render
from django.http import JsonResponse
from WeHealedAPI.models import Dictionary
from WeHealedAPI.serializers import WeHealedAPISerializer

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
                word = dic.get(abbreviation_text=tok)
                result += word.original_text + ' '
            except Dictionary.DoesNotExist:
                result += tok + ' '
        
    return JsonResponse({'status': status, 'result': result})
        
    t = 'LV'
    dic = Dictionary.objects.get(abbreviation_text=t)
    print(dic.original_text)
    # print(Dictionary.get(abbreviation_text='RWMA'))
    req_data = request.GET.get('a')
    # req_data = Dictionary.objects.get('abbreviation_text')
    return JsonResponse({'req_data': req_data})

def test(request):
    txt = request.GET.get('text', '')
    return JsonResponse({'req_data': txt})
