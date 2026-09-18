import pandas
import os
import datetime
import scraping

def search_nfu(date:str=None, feedsource:str=None, title:str=None) -> pandas.DataFrame:
    '''
    參數為篩選條件：
        date:   三種格式，yyyy-mm-dd 或 yyyy-mm 或 yyyy
        feedsource: 單位
        title:  標題包含什麼
    不一定三個都要，視需求
    
    當程式找不到今日抓到的檔案，將會自動進行抓取
    '''
    def search_day(date:str, dataframe:pandas.DataFrame) -> tuple:
        '''
        回傳 (error, newdata)
        error:bool
        newdata:pandas.DataFrame
        '''
        
        # 檢查 date 格式        
        date = date.split('-')
        o = len(date)
        if o == 0:
            return (True, dataframe)
        elif o == 1:
            if len(date[0])!=4:
                return (True, dataframe) 
        elif o == 2:
            if len(date[0])!=4 or len(date[1])!=2:
                return (True, dataframe) 
        elif o == 3:
            if len(date[0])!=4 or len(date[1])!=2 or len(date[2])!=2:
                return (True, dataframe) 
        else:
            return (True, dataframe) 
            
        
        try:
            date = list(map(int, date))
        except:
            return (True, dataframe)
        
        
        
        newdata = pandas.DataFrame()
                    
        # year
        if o == 1:
            u = f"{date[0]:04d}"
            newdata = dataframe[ dataframe["date"].str[:4].str.contains(u) ]
        # year-month
        elif o == 2:
            u = f"{date[0]:04d}-{date[1]:02d}"
            newdata = dataframe[ dataframe["date"].str[:7].str.contains(u) ]
        elif o == 3:
            u = f"{date[0]:04d}-{date[1]:02d}-{date[2]:02d}"
            newdata = dataframe[ dataframe["date"].str.contains(u) ]
        
        return (False, newdata)

    def search_feedsource(feedsource:str, dataframe:pandas.DataFrame) -> pandas.DataFrame:
        newdata = dataframe[ dataframe["feedsource"].str.contains(feedsource) ]
        return newdata
    
    def search_title(title:str, dataframe:pandas.DataFrame) -> pandas.DataFrame:
        newdata = dataframe[ dataframe["title"].str.contains(title) ]
        return newdata
    
    today = datetime.datetime.today().date()
    # print(os.listdir("nfu"))
    # print(f"{today}.csv")
    if not(f"{today}.csv" in os.listdir("nfu")):
        scraping.scrap_nfu_to_csv()
    
    data = pandas.read_csv(f"nfu\\{today}.csv")
    
    
    errorMessage = []
    
    
    if not(date is None):
        error, data = search_day(date, data)
        if error:
            errorMessage.append("date 格式錯誤")
        
    if not(feedsource is None):
        data = search_feedsource(feedsource, data)
    
    if not(title is None):
        data = search_title(title, data)
    
    if errorMessage:
        print("\n".join(errorMessage))
        return 
    else:
        data = data.reset_index(drop=True)
        return data