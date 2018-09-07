# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render
from django.http import JsonResponse
from WeHealedAPI.models import Dictionary
from WeHealedAPI.serializers import WeHealedAPISerializer

import csv
from bs4 import BeautifulSoup
import requests

# Create your views here.
def detail(request):
    t = 'LV'
    dic = Dictionary.objects.get(abbreviation_text=t)
    print(dic.original_text)
    # print(Dictionary.get(abbreviation_text='RWMA'))
    req_data = request.GET.get('a')
    # req_data = Dictionary.objects.get('abbreviation_text')
    return JsonResponse({'req_data': req_data})

def dbinit(request):
    
    # f = open('data.csv', 'r')
    # rdr = csv.reader(f)
    # result = ''
    # tt = 0
    # flag = False
    # for line in rdr:
    #     tmp = list(line)
    #     fmt = u'{:<15}'*len(tmp)
    #     if flag:
    #         dic_data = Dictionary(abbreviation_text = tmp[2], original_text = tmp[3], organization_text = "", detail_text = "")
    #         dic_data.save()
    #         result += fmt.format(*[s.decode('utf-8') for s in tmp])
    #     else: flag = True
    #     tt += 1
    #     # if tt == 3: break
    # f.close()
    
    
#     fb = Dictionary.objects.all()
#     fb.delete()
    
    
#     max_page = 8
#     url = "http://www.medicabbreviations.com/cat/heart"
    
#     for page in range(1, max_page+1):
#         now_url = url + "-p" + str(page) + ".html"
#         req = requests.get(now_url, allow_redirects=False)
#         plain_text = req.text
#         bs = BeautifulSoup(plain_text, 'html.parser')
        
#         abbreviations = []
#         originals = []
        
#         l = bs.find_all('a')
#         for s in l:
#             try:
#                 prop = s.get('class')
#                 if prop != None and prop[0] == "special":
#                     abbreviations.append(s.get_text())
#             except UnicodeEncodeError:
#                 pass
            
#         l = bs.find_all("td", width=True)
#         for s in l:
#             try:
#                 prop = s["width"]
#                 if prop != None and prop == "450":
#                     originals.append(s.get_text())
#             except UnicodeEncodeError:
#                 pass
            
#         for (abb, org) in zip(abbreviations, originals):
#             dat = Dictionary(abbreviation_text=abb, original_text=org, organization_text="heart", detail_text="")
#             dat.save()
    
#     return JsonResponse({'abb_size': str(len(abbreviations)), 'org_size': str(len(originals))})
    
    # dic = Dictionary.objects.all()
    
    # count = 0
    
    # for d in dic:
    #     abbreviation = d.abbreviation_text
    #     originals = d.original_text.split(';')
    #     organization = d.organization_text
    #     if len(originals) == 1:
    #         d.original_text = d.original_text.strip()
    #         d.save()
    #         count += 1
    #         continue
    #     for original in originals:
    #         newdic = Dictionary(abbreviation_text=abbreviation, original_text=original, organization_text=organization, detail_text="")
    #         newdic.save()
    #     d.delete()
    
    return JsonResponse({'result': ''})