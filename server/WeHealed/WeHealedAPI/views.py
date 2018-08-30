# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render
from django.http import JsonResponse
from WeHealedAPI.models import Dictionary
from WeHealedAPI.serializers import WeHealedAPISerializer

# Create your views here.
def detail(request):
    t = 'LV'
    dic = Dictionary.objects.get(abbreviation_text=t)
    print(dic.original_text)
    # print(Dictionary.get(abbreviation_text='RWMA'))
    req_data = request.GET.get('a')
    # req_data = Dictionary.objects.get('abbreviation_text')
    return JsonResponse({'req_data': req_data})
