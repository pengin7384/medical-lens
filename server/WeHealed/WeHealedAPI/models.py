# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models

# Create your models here.
class Dictionary(models.Model):
    abbreviation_text = models.CharField(max_length=200)
    original_text = models.CharField(max_length=500)
    organization_text = models.CharField(max_length=200)
    detail_text = models.CharField(max_length=5000)
    
    def __str__(self):
        return self.abbreviation_text
