# -*- coding: utf-8 -*-
# Generated by Django 1.11.12 on 2018-08-18 08:01
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('WeHealedAPI', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Dictionary',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('abbreviation_text', models.CharField(max_length=200)),
                ('original_text', models.CharField(max_length=500)),
                ('organization_text', models.CharField(max_length=200)),
            ],
        ),
        migrations.DeleteModel(
            name='saying',
        ),
    ]
