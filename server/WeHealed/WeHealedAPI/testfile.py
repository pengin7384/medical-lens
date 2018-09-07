import os
from WeHealedAPI.models import DataSet

path = os.getcwd()

infile = open(path + '/SentenceData/infile.txt', 'r', encoding='UTF8')
outfile = open(path + '/SentenceData/outfile.txt', 'w', encoding='UTF8')

data_set = DataSet.objects

while True:
    line = infile.readline()
    if not line: break
     
    """ translate """
    target = 'ko'
    translate_client = translate.Client()

    if isinstance(line, six.binary_type):
        line = text.decode('utf-8')

    result = translate_client.translate(line, target_language=target)
    result_line = result['translatedText']
    print(u'***\nText: {}'.format(result['input']))
    print(u'Translation: {}'.format(result_line))

    outfile.write()
    # DB에 넣어야 함
    # dataset.create(origin_text=line, translated_text=result_line)

infile.close()
outfile.close()
