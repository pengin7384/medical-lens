# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render
from django.http import JsonResponse
from WeHealedAPI.models import Dictionary, Pedia, DataSet, RuleDataSet, FlittoCallbackDB
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

# rule
import re

import json

# 일단 CSRF를 끄고 구현
from django.views.decorators.csrf import csrf_exempt

@csrf_exempt
def receive_result(request):
    # POST Request에 대해서만 처리
    if request.method == 'POST':
        # 요청받은 POST 데이터를 Dictionary 자료형으로 변환
        request_data = json.loads(request.body)
        
        # 데이터 형식 예시(Json)
        # {
        #     "meta": {
        #         "code": 200,
        #         "message": "success"
        #     },
        #     "data": {
        #         "src_lang_code": "en",
        #         "dst_lang_code": "ko",
        #         "cp_transaction_id": "transaction_key_1233",
        #         "translation": "한국 정부는 가상화폐의 돈세탁 및 기타 불법 활동에 사용되는 것 을 막기 위해 거래에서 익명의 은행 계좌 사용을 금지하면서 화요일에 실제 현금 거래 시스템 을 시작했다.",
        #         "callback_url": "https://example.com/callback/123",
        #         "cp_content_id": "content_key_123",
        #         "confirm_url": "https://api.flit.to/v1/pro/translation/12345/confirm?answer=Y"
        #     }
        # }

        try:
            meta = request_data['meta']
            # 응답코드가 200(정상응답코드)이 아닐 경우 실패 응답
            if meta['code'] != 200:
                return JsonResponse({'result': 'fail', 'reason': meta['message']})

            data = request_data['data']

            # DB 저장용 데이터 추출
            src_lang_code = data['src_lang_code']
            dst_lang_code = data['dst_lang_code']
            cp_transaction_id = data['cp_transaction_id']
            translation = data['translation']
            callback_url = data['callback_url']
            cp_content_id = data['cp_content_id']
            confirm_url = data['confirm_url']

            # 기존에 동일한 Primary Key로 저장된 데이터가 있을 경우 삭제하고 현재 요청받은 데이터로 새로 저장
            try:
                flitto_callback_data = FlittoCallbackDB.objects.get(cp_transaction_id=cp_transaction_id)
                flitto_callback_data.delete()
            except FlittoCallbackDB.DoesNotExist:
                pass
            finally:
                flitto_callback_data = FlittoCallbackDB(src_lang_code=src_lang_code,
                                                        dst_lang_code=dst_lang_code,
                                                        cp_transaction_id=cp_transaction_id,
                                                        translation=translation,
                                                        callback_url=callback_url,
                                                        cp_content_id=cp_content_id,
                                                        confirm_url=confirm_url)
                flitto_callback_data.save()

            # 저장 성공 시 DB 저장 시간과 성공 코드 응답
            return JsonResponse({'result': 'success', 'reason': flitto_callback_data.callback_time})
        except KeyError:
            return JsonResponse({'result': 'fail', 'reason': 'Occur KeyError'})

    else:
        return JsonResponse({'result': 'fail', 'reason': 'Try POST Method please'})

    
def check_callback(request):
    dic = Dictionary.objects
    cp_transaction_id = request.GET.get('cp_transaction_id', '')
    if len(cp_transaction_id) == 0:
        return JsonResponse({'result': 'fail', 'reason': 'Input cp_transaction_id'})
    
    try:
        flitto_callback_data = FlittoCallbackDB.objects.get(cp_transaction_id=cp_transaction_id)
    except FlittoCallbackDB.DoesNotExist:
        return JsonResponse({'result': 'success', 'reason': False})
    
    return JsonResponse({'result': "success", 'reason': True})
