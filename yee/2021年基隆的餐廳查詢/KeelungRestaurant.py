import json.scanner
import pandas as pd
import numpy as np
import bluePotato
import json


with open(file = 'KeelungRestaurant.json', mode = 'rt', encoding = 'utf-8') as JsonFile:
    data = json.load(JsonFile)
    data = bluePotato.json.rowColumn(data)
    chose = pd.Series([data['address'][row][3:6] == '中正區' for row in range(data['id'].size)])
    data = data[chose]
    pd.set_option('display.max_columns', None)
    pd.set_option('display.max_rows', None)
    pd.set_option('max_colwidth', 100)
    print(data[['title', 'address']])