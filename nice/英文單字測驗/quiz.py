import os
import csv
import random

def main():
    is_word = ask_word_or_phrase()
    file_name = ask_file_name()
    path = get_file_path(is_word, file_name)

    if not os.path.exists(path):
        print("找不到檔案，請確認檔名和類別。")
        return

    entries = load_csv(path)
    if not entries:
        print("檔案內容為空。")
        return

    num_questions = ask_num_questions(len(entries)-1)
    run_quiz(entries, num_questions)


def ask_word_or_phrase():
    while True:
        a = input("單字輸入 1\n片語輸入 0\n")
        if a in ('1', '0'):
            return bool(int(a))


def ask_file_name():
    name = input("檔名?\n(不用打副檔名)\n")
    return name.strip() + '.csv'


def get_file_path(is_word, file_name):
    base = os.path.dirname(os.path.abspath(__file__))
    folder = '單字' if is_word else '片語'
    return os.path.join(base, folder, file_name)


def load_csv(path):
    with open(path, mode='r', encoding='utf-8') as file:
        reader = csv.DictReader(file)
        return list(reader)


def ask_num_questions(max_num):
    while True:
        try:
            num = int(input(f"要測驗幾題？(最多 {max_num} 題)\n"))
            if 1 <= num <= max_num:
                return num
        except:
            pass
        print("請輸入有效數字。")
        
def run_quiz(entries, num_questions):
    selected = random.sample(entries, num_questions)
    correct = 0

    print("\n=== 測驗開始 ===\n")

    for i, entry in enumerate(selected, 1):
        meaning = entry['解釋']
        pos = entry.get('詞性', '').strip()
        synonyms_raw = entry.get('同義詞', '')
        expected_set = set()

        # 收集正確答案：單字本身 + 同義詞
        expected_set.add(entry['單字/片語'].strip().lower())
        if synonyms_raw:
            expected_set.update(s.strip().lower() for s in synonyms_raw.split(';') if s.strip())

        print(f"{i}. 解釋：「{meaning}」")
        if pos:
            print(f"   詞性：{pos}")
        print(f"   請輸入所有答案，用分號 ; 分隔（共 {len(expected_set)} 個）")

        # 使用者輸入
        user_input = input("> ").strip().lower()
        user_set = set(s.strip() for s in user_input.split(';') if s.strip())

        if len(user_set & expected_set) == len(expected_set):
            print("正確！\n")
            correct += 1
        else:
            print(f"錯誤\n你輸入了：{';'.join(user_set)}")
            print(f"正確答案是：{';'.join(expected_set)}\n")

    print(f"測驗結束，共 {num_questions} 題，答對 {correct} 題，正確率 {round(correct / num_questions * 100)}%")
