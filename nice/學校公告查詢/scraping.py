import requests
from bs4 import BeautifulSoup
from bs4 import _typing
import datetime

def __stopdate(today, mode:int):
    '''
    mode=1: 一週內
    mode=2: 三個月內
    '''
    def __mode1(today):    
        monthdays = [0, 31, 28, 31, 30, 31, 30, 31,
                     31, 30, 31, 30, 31]
        if today.year%400==0 or today.year%4==0 and today.year%100!=0:
            monthdays[2] = 29
        
        stopyear, stopmonth, stopday = 0,0,0
        if today.day >= 7:
            stopyear, stopmonth, stopday = today.year, today.month, today.day-6
        elif today.month > 1:
            stopyear, stopmonth, stopday = today.year, today.month, monthdays[today.month-1]+today.day-6
        else:
            stopyear, stopmonth, stopday = today.year-1, 12, 31+today.day-6
        
        return (stopyear,stopmonth,stopday)
        
    def __mode2(today):    
        stopyear, stopmonth, stopday = 0,0,today.day
        if today.month>3:
            stopyear, stopmonth = today.year, today.month-3
        else:
            stopyear, stopmonth = today.year-1, 12-today.month
    
        return (stopyear,stopmonth,stopday)
    
    if mode == 1:return __mode1(today)
    if mode == 2:return __mode2(today)



def scrap_nfu_to_csv():
    def __scrap_to_csv() -> None:
        today = datetime.datetime.today().date()
        stopyear, stopmonth, stopday = __stopdate(today, 1)
            
        page = 0
        run = True
        
        with open(file=f'nfu/{today}.csv', mode='wt', encoding='utf-8') as f:
            # 標題列
            print("\"date\",\"feedsource\",\"title\",\"link\"",file=f)
            # 內容
            data = []
            
            while run:
                page += 1
                url = f"https://www.nfu.edu.tw/zh_tw/ann/adm?page_no={page}&category%5B%5D=68db3e8708577793ef6894cc&tags%5B%5D=all"
                headers = {"User-Agent": "Mozilla/5.0"}
                soup = __request_page(url, headers)
                tag = __find_tag(soup)
                
                all_rows = tag.find_all("tr")
                        
                for row in all_rows:
                    postdate = row.find("td", class_="i-annc__postdate")
                    if postdate is None:
                        continue
                    
                    date = postdate.get_text(strip=True)
                    
                    postyear,postmonth,postday = map(int, date.split('-'))
                    
                    if postyear < stopyear:
                        run = False
                        break
                    elif postyear >= stopyear:
                        if postmonth < stopmonth or postmonth == stopmonth and postday < stopday:
                            run = False
                            break         
                    
                    content = row.find("td", class_="i-annc__content")
                    title = content.get_text(strip=True)
                    
                    feedsource = content.find("a", class_ = "feed-source").get_text(strip=True)
                    
                    link = content.find("a", class_="i-annc__title")
                    herf = "https://nfu.edu.tw"+link["href"]
                    
                    
                    data.append(f"\"{date}\",\"{feedsource}\",\"{title}\",\"{herf}\"")
            
            if not(data is None):
                print()
                print("\n".join(data), end="", file=f)


    def __request_page(url:str, headers:dict) -> None:
        response = requests.get(url, headers=headers, verify=False)  # 加上 verify=False 取消憑證檢查
        if response.status_code==200: print("連線成功") # 200 代表請求成功
        soup = BeautifulSoup(response.text, "html.parser")
        
        return soup

    def __find_tag(soup:BeautifulSoup) -> _typing._AtMostOneTag:
        # 開始剝開 div
            # <body class="internal-page accessibility_mode prohibit_proxy_feature banner_height_setting_0 no_orbit_bar" data-module="announcement">
                # <div class="verticalhome">
                    # <div class="layout-content">
                        # <div class="layout-content-inner inner-page contentwrap3">
                            # <div class="row container membercontainer">
                                # <section class="extrabox layout-content-box left-column col-sm-9">
                                    # <main id="main-content" class="main-content" data-content="true">
                                        # <div class="i-annc  index-announcement-14 ">
                                            # <table class="i-annc__table  table  table-striped">
                                        
        layout_content = soup.find("div", class_="layout-content")
        layout_inner = layout_content.find("div", class_="layout-content-inner")
        row_container = layout_inner.find("div", class_="row container membercontainer")
        section = row_container.find("section", class_="extrabox")
        main_content = section.find("main", id="main-content")
        i_annc = main_content.find("div", class_="i-annc")
        i_annc_table = i_annc.find("table", class_="i-annc__table")
        
        return i_annc_table
    
    __scrap_to_csv()

def scrap_nfultc_to_csv():
    def __scrap_to_csv() -> None:
        today = datetime.datetime.today().date()
        stopyear, stopmonth, stopday = __stopdate(today, 1)
        
        page = 0
        run = True
        
        with open(file=f'nfultc/{today}.csv', mode='wt', encoding='utf-8') as f:
            # 標題列
            print("\"date\",\"feedsource\",\"title\",\"link\"", end="", file=f)
            # 內容
            data = []
            
            while run:
                page += 1
                url = f"https://ltc.nfu.edu.tw/zh_tw/news/announcements/2026ann?page_no={page}&"
                headers = {"User-Agent": "Mozilla/5.0"}
                soup = __request_page(url, headers)
                tag = __find_tag(soup)
                
                all_rows = tag.find_all("tr")
                        
                for row in all_rows:
                    postdate = row.find("td", class_="i-annc__postdate")
                    if postdate is None:
                        continue
                    
                    date = postdate.get_text(strip=True)
                    
                    postyear,postmonth,postday = map(int, date.split('-'))
                    if postyear < stopyear:
                        run = False
                        break
                    elif postyear == stopyear:
                        if postmonth < stopmonth or postmonth == today.month and postday < stopday:
                            run = False
                            break
                    
                    content = row.find("td", class_="i-annc__content")
                    title = content.get_text(strip=True)
                    
                    
                    link = content.find("a", class_="i-annc__title")
                    herf = "https://ltc.nfu.edu.tw"+link["href"]
                    
                    
                    data.append(f"\"{date}\",\"{title}\",\"{herf}\"")
            
            if not(data is None):
                print()
                print("\n".join(data), end="", file=f)

    def __request_page(url:str, headers:dict) -> None:
        response = requests.get(url, headers=headers, verify=False)  # 加上 verify=False 取消憑證檢查
        if response.status_code==200:print("連線成功") # 200 代表請求成功
        soup = BeautifulSoup(response.text, "html.parser")
        
        return soup

    def __find_tag(soup:BeautifulSoup) -> _typing._AtMostOneTag:
        # 開始剝開 div
            # <body class="internal-page accessibility_mode prohibit_proxy_feature banner_height_setting_0 no_orbit_bar" data-module="announcement">
                # <div class="verticalhome">
                    # <div class="layout-content">
                        # <div class="layout-content-inner inner-page contentwrap3">
                            # <div class="row  container">
                                # <section class="extrabox layout-content-box right-column col-sm-9">
                                    # <main id="main-content" class="main-content" data-content="true">
                                        # <div class="i-annc  index-announcement-14 ">
                                            # <table class="i-annc__table  table  table-striped">
                                        
        layout_content = soup.find("div", class_="layout-content")
        layout_inner = layout_content.find("div", class_="layout-content-inner")
        i_annc = layout_inner.find("div", class_="i-annc")
        i_annc_table = i_annc.find("table", class_="i-annc__table")
        
        return i_annc_table
    
    __scrap_to_csv()