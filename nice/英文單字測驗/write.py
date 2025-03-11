import os

def main():
    New = new_or_old()
    Damn = damn_or_pamn()
    if New:
        fileName = ask_file_name()
        build_new_file( Damn, fileName )
    else:
        while True:
            fileName = ask_file_name()
            if test_old_name( Damn, fileName ):
                break


# 開啟舊檔或建立新檔
def new_or_old():    
    while True:
        a = input("建立新檔輸入 1\n開啟舊檔輸入 0\n")
        if a == '1' or a == '0':
            return bool(int(a))
        
# 單字或片語
def damn_or_pamn():    
    while True:        
        a = input("單字輸入 1\n片語輸入 0\n")        
        if a == '1' or a == '0':
            return bool(int(a))

# 要不要詞性
def ask_need_part_of_peech():    
    while True:
        a = input("要詞性的話輸入 1\n不要的話輸入 0\n")
        if a == '1' or a == '0':
            return bool(int(a))

# 問檔名
def ask_file_name():
    a = input("檔名?\n(不用打副檔名)\n")
    return a + '.csv'

# 建立新檔
def build_new_file( Damn, fileName ):
    if Damn:
        with open( file = '單字/' + fileName, mode = 'wt', encoding = 'utf-8' ) as file:
            pass
    else:
        with open( file = '片語/' + fileName, mode = 'wt', encoding = 'utf-8' ) as file:
            pass
    
    return

# 檢查舊檔是否存在
def test_old_name( Damn, fileName ):
    fileList = os.listdir('單字' if Damn else '片語')
    return fileName in fileList