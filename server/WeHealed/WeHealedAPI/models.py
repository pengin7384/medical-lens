# -*- coding: utf-8 -*-
from __future__ import unicode_literals
from django.utils.safestring import mark_safe

from django.db import models

# 약어 사전
class Dictionary(models.Model):
    abbreviation_text = models.CharField(max_length=200)
    original_text = models.CharField(max_length=500)
    organization_text = models.CharField(max_length=200)
    detail_text = models.CharField(max_length=5000)
    code = models.IntegerField(default=10000)
    
    def __str__(self):
        return self.abbreviation_text

# 설명 url
class Pedia(models.Model):
    name = models.CharField(max_length=200)
    url = models.CharField(max_length=1000)
    score = models.IntegerField(default=10)
    reply_total = models.IntegerField(default=0)
    reply_yes = models.IntegerField(default=0)
    reply_no = models.IntegerField(default=0)
    dictionary = models.IntegerField(default=0)
    # dictionary = models.ForeignKey(Dictionary, on_delete=models.DO_NOTHING, db_constraint=False, null=True)

    def __str__(self):
        return self.name 
                             
                             
# 입력 문장과 번역 결과 모으는 테이블
class DataSet(models.Model):
    origin_sentence     = models.CharField(max_length=2000)    # 사진->텍스트 변환 문장
    translated_sentence = models.CharField(max_length=2000)    # (임시) 구글 번역 돌린 결과
    def __str__(self):
        return self.origin_sentence
    
    
# 여러 단어가 하나의 뜻으로 해석되는 경우
class RuleDataSet(models.Model):
    origin_sentence         = models.CharField(max_length=2000)
    translated_sentence     = models.CharField(max_length=2000)
    size                    = models.IntegerField(default=0)    # 1,2,3
    def __str__(self):
        return self.origin_sentence
    

class MedicalRecordImageDB(models.Model):
    filename = models.CharField(max_length=200)
    image = models.ImageField(upload_to="MedicalRecordImages/")

    def image_tag(self):
        if self.image:
            return mark_safe('<img src="%s" style="width: 45px; height:45px;" />' % self.image.url)
        else:
            return 'No Image Found'

    image_tag.short_description = 'Image'

    def __str__(self):
        return self.filename
    
    
class FlittoCallbackDB(models.Model):
    src_lang_code = models.CharField(max_length=10)
    dst_lang_code = models.CharField(max_length=10)
    cp_transaction_id = models.CharField(primary_key=True, max_length=100)
    translation = models.CharField(max_length=5000)
    callback_url = models.CharField(max_length=200)
    cp_content_id = models.CharField(max_length=200)
    confirm_url = models.CharField(max_length=200)
    callback_time = models.DateTimeField(auto_now_add=True, editable=False, blank=True)

    def __str__(self):
        return self.cp_transaction_id


    