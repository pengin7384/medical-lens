# -*- coding: utf-8 -*-
# query_exec.py 파일 복제해서 작업용으로 수정함

from __future__ import unicode_literals

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
import json

# rule
import re

import sqlite3
 
# SQLite DB 연결
conn = sqlite3.connect("db.sqlite3")
 
# Connection 으로부터 Cursor 생성
cur = conn.cursor()
 
# SQL 쿼리 실행
# cur.execute("SELECT * FROM WeHealedAPI_dictionary as Dic WHERE Dic.original_text=='RWMA'")
cur.execute("SELECT pedia.id, pedia.name, pedia.dictionary, Dic.id, Dic.abbreviation_text, Dic.original_text, Dic.organization_text FROM WeHealedAPI_pedia as pedia, WeHealedAPI_dictionary as Dic WHERE pedia.name == Dic.abbreviation_text")
# organization_txt == heart -> code=10000 / lung -> code=20000로 바꿔야함
# cur.execute("SELECT pedia.id, pedia.name, pedia.dictionary, Rule.id, Rule.origin_sentence FROM WeHealedAPI_pedia as pedia, WeHealedAPI_RuleDataSet as Rule WHERE pedia.name == Rule.origin_sentence")



# 데이타 Fetch
rows = cur.fetchall()
print (len(rows))
for row in rows:
    print(row)
 
# Connection 닫기
conn.close()

# 실행법 : 터미널에서 python ./query_exec.py 실행
