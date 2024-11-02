import pandas as pd
import math as mm
import numpy as np
import wget as www

class append:
    class Total:
        def column(data:pd.DataFrame, columnName:str = 'total') -> pd.DataFrame:
            '''
            data: 資料來源
            columnName: 新欄名，預設"total"
            '''
        def column(data:pd.DataFrame, columnName:str = 'total') -> pd.DataFrame:
            '''
            data: 資料來源
            columnName: 新欄名，預設"total"
            '''
            data[columnName] = sum([data[i] for i in data.columns])
            return data
        
        def row(data:pd.DataFrame) -> pd.DataFrame:
            '''
            data: 資料來源
            '''
        def row(data:pd.DataFrame) -> pd.DataFrame:
            '''
            data: 資料來源
            '''
            data = data._append({i:data[i].sum() for i in data.columns}, ignore_index = True)
            return data
        
    class Average:
        def column(data:pd.DataFrame, columnName:str = 'total', f:int = 3, mode:int = 0) -> pd.DataFrame:
            '''
            data: 來源資料，型別一定要DataFrame
            colunName: 新欄的欄名
            f: mode = 0 :不額外處理

               mode = 1 :四捨五入到小數點後f位
               mode = 2 :四捨五入到10**f 

               mode = 3 :無條件捨去到小數點後f位
               mode = 4 :無條件捨去到10**f 

               mode = 5 :無條件進位到小數點後f位
               mode = 6 :無條件進位到10**f 
            '''
            columns = pd.Series(data.columns)
            average = sum([data[i] for i in columns])/columns.size
            t = (10**f)
            if mode == 0:
                data[columnName] = sum([data[i] for i in columns])/columns.size
            if mode == 1:
                data[columnName] = round(average,f)
            elif mode == 2:
                data[columnName] = round(average//t*10)*t/10
                data[columnName] = round(average//t*10)*t/10
            
            elif mode == 3:
                f-=1
                data[columnName] = np.ceil(average/t)*t
                f-=1
                data[columnName] = np.ceil(average/t)*t
            elif mode == 4:
                data[columnName] = np.ceil(average*t)/t
                data[columnName] = np.ceil(average*t)/t
            return data 
        
        def row(data:pd.DataFrame, f:int = 3, mode:int = 0) -> pd.DataFrame:
            '''
            data: 來源資料，型別一定要DataFrame
            rowName: 新列名
            f: mode = 0 :不額外處理

               mode = 1 :四捨五入到小數點後f位
               mode = 2 :四捨五入到10**f 

               mode = 3 :無條件捨去到小數點後f位
               mode = 4 :無條件捨去到10**f 

               mode = 5 :無條件進位到小數點後f位
               mode = 6 :無條件進位到10**f 
            '''
            columns = pd.Series(data.columns)
            t = (10**f)
            if mode == 0:
                average = {column:data[column].sum()/data[column].size for column in columns}
                data = data._append(average, ignore_index = True)
            if mode == 1:
                average = {column:round(data[column].sum()/data[column].size,f) for column in columns}
                data = data._append(average, ignore_index = True)
            elif mode == 2:
                average = {column:round(data[column].sum()/data[column].size/(10**(f-1)))*(10**(f-1)) for column in columns}
                data = data._append(average, ignore_index = True)
            
            elif mode == 3:
                f-=1
                average = {column:np.ceil(data[column].sum()/data[column].size/(10**f))*(10**f) for column in columns}
                data = data._append(average, ignore_index = True)
            elif mode == 4:
                average = {column:np.ceil(data[column].sum()/data[column].size*(10**f))/(10**f)  for column in columns}
                data = data._append(average, ignore_index = True)
            return data 
        def row(data:pd.DataFrame, f:int = 3, mode:int = 0) -> pd.DataFrame:
            '''
            data: 來源資料，型別一定要DataFrame
            rowName: 新列名
            f: mode = 0 :不額外處理

               mode = 1 :四捨五入到小數點後f位
               mode = 2 :四捨五入到10**f 

               mode = 3 :無條件捨去到小數點後f位
               mode = 4 :無條件捨去到10**f 

               mode = 5 :無條件進位到小數點後f位
               mode = 6 :無條件進位到10**f 
            '''
            columns = pd.Series(data.columns)
            t = (10**f)
            if mode == 0:
                average = {column:data[column].sum()/data[column].size for column in columns}
                data = data._append(average, ignore_index = True)
            if mode == 1:
                average = {column:round(data[column].sum()/data[column].size,f) for column in columns}
                data = data._append(average, ignore_index = True)
            elif mode == 2:
                average = {column:round(data[column].sum()/data[column].size/(10**(f-1)))*(10**(f-1)) for column in columns}
                data = data._append(average, ignore_index = True)
            
            elif mode == 3:
                f-=1
                average = {column:np.ceil(data[column].sum()/data[column].size/(10**f))*(10**f) for column in columns}
                data = data._append(average, ignore_index = True)
            elif mode == 4:
                average = {column:np.ceil(data[column].sum()/data[column].size*(10**f))/(10**f)  for column in columns}
                data = data._append(average, ignore_index = True)
            return data 

class json:
    def rowColumn( data:dict ) -> pd.DataFrame:
        '''
        data: 來源資料
        '''
        name = list(data.keys())[0]
        data = data[name]
        columns = []
        rows = range(len(data))
        for row in rows:
            for column in list(data[row].keys()):
                if not(column in columns):
                    columns.append(column)
        Data = {}
        for column in columns:
            Data[column] = []
            for row in rows:
                if column in set(data[row].keys()):
                    Data[column].append(data[row][column])
                else:
                    Data[column].append("無")
        
        return pd.DataFrame(Data)

class wget:
    def downloadfile( url:str, fileName:str, *, location:str = 'C:\\Users\\jinyu\\Desktop' ):
        '''
        url : 連結網址
        fileName : 檔名
        '''
        www.download( url, f'{location}\\{fileName}')