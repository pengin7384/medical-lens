# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.contrib import admin
from WeHealedAPI.models import Dictionary, Pedia, DataSet, RuleDataSet, MedicalRecordImageDB, FlittoCallbackDB

# Register your models here.
admin.site.register(Dictionary)
admin.site.register(Pedia)
admin.site.register(DataSet)
admin.site.register(RuleDataSet)


# admin 페이지 DB에 image 표시를 위한 설정
class MedicalRecordImageDBAdmin(admin.ModelAdmin):
    readonly_fields = ('image',)
    list_display = ('filename', 'image', 'image_tag')

    
admin.site.register(MedicalRecordImageDB, MedicalRecordImageDBAdmin)


# admin 페이지 FlittoCallbackDB에 callback_time 표시를 위한 설정
class FlittoCallbackDBAdmin(admin.ModelAdmin):
    readonly_fields = ('callback_time',)


admin.site.register(FlittoCallbackDB, FlittoCallbackDBAdmin)

