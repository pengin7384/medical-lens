# -*- coding: utf-8 -*-
# Generated by Django 1.11.12 on 2018-09-01 07:15
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('WeHealedAPI', '0003_dictionary_detail_text'),
    ]

    operations = [
        migrations.CreateModel(
            name='Pedia',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('url', models.CharField(max_length=1000)),
                ('score', models.IntegerField()),
            ],
        ),
    ]
