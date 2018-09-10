# -*- coding: utf-8 -*-

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
cur.execute("SELECT count(*) FROM WeHealedAPI_dictionary")
 
# 데이타 Fetch
rows = cur.fetchall()
for row in rows:
    print(row)
 
# Connection 닫기
conn.close()

# 실행법 : 터미널에서 python ./query_exec.py 실행
