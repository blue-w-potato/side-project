import quiz
import write

def main():
    while True:
        # 已移除寫入模式
        # a = input("輸入 1 是寫入模式\n輸入 2 是測驗模式\n輸入 q 離開\n")
        
        a = input("輸入 1 寫入模式\n輸入 2 測驗模式\n輸入 q 離開\n")
        
        if a == '1':
            write.main()
        elif a == '2':
            quiz.main()
        elif a.lower() == 'q':
            print("再見！")
            break
        else:
            print("請輸入有效的選項（1、2 或 q）")

if __name__ == "__main__":
    main()
