# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render
from django.http import JsonResponse
from WeHealedAPI.models import Dictionary, Pedia, DataSet, RuleDataSet
from WeHealedAPI.serializers import WeHealedAPISerializer
from django.http import HttpResponse

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

import csv
from bs4 import BeautifulSoup
import requests

# Create your views here.
def home(request):
    return HttpResponse("<br><br><h1>WeHealed</h1><br><h2>Medical Lens Project</h2>")


