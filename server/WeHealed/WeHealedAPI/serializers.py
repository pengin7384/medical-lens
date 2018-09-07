# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from rest_framework import serializers
from WeHealedAPI.models import Dictionary, Pedia

class WeHealedAPISerializer(serializers.ModelSerializer):
    class Meta:
        model = Dictionary
        fields = '__all__'

