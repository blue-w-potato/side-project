import os
import csv

def main():
    New = new_or_old()
    Damn = damn_or_pamn()
    if New:
        fileName = ask_file_name()
        build_new_file(Damn, fileName)
    else:
        while True:
            fileName = ask_file_name()
            if test_old_name(Damn, fileName):
                break
            else:
                print("找不到檔案，請重新輸入。")

    need_pos = ask_need_part_of_peech()
    path = get_file_path(Damn, fileName)

    write_words(path, need_pos)


# 開啟舊檔或建立新檔
def new_or_old():    
    while True:
        a = input("建立新檔輸入 1\n開啟舊檔輸入 0\n")
        if a == '1' or a == '0':
            return bool(int(a))
        else:
            print("請輸入 1 或 0")

# 單字或片語
def damn_or_pamn():    
    while True:        
        a = input("單字輸入 1\n片語輸入 0\n")        
        if a == '1' or a == '0':
            return bool(int(a))
        else:
            print("請輸入 1 或 0")

# 要不要詞性
def ask_need_part_of_peech():    
    while True:
        a = input("要詞性的話輸入 1\n不要的話輸入 0\n")
        if a == '1' or a == '0':
            return bool(int(a))
        else:
            print("請輸入 1 或 0")

# 問檔名
def ask_file_name():
    a = input("檔名?\n(不用打副檔名)\n")
    return a.strip() + '.csv'

# 建立新檔
def build_new_file(Damn, fileName):
    path = get_file_path(Damn, fileName)
    folder = os.path.dirname(path)
    os.makedirs(folder, exist_ok=True)

    with open(path, mode='w', encoding='utf-8', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['單字/片語', '詞性', '解釋', '同義詞'])

# 檢查舊檔是否存在
def test_old_name(Damn, fileName):
    path = get_file_path(Damn, fileName)
    return os.path.exists(path)

# 取得完整檔案路徑
def get_file_path(Damn, fileName):
    base = os.path.dirname(os.path.abspath(__file__))
    subfolder = '單字' if Damn else '片語'
    return os.path.join(base, subfolder, fileName)

# 開始輸入單字/片語
def write_words(path, need_pos):
    print("\n開始輸入內容（輸入空白可結束）")
    with open(path, mode='a', encoding='utf-8', newline='') as file:
        writer = csv.writer(file)

        while True:
            word = input("單字/片語：").strip()
            if word == '':
                break

            pos = input("詞性：").strip() if need_pos else ''
            meaning = input("解釋：").strip()
            synonyms = input("同義詞（可有空格，多個用分號 ; 分隔）：").strip()

            writer.writerow([word, pos, meaning, synonyms])
    print("輸入完成！")

