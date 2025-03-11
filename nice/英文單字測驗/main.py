import quiz
import write


def main():
    while True:
        
        a = input("輸入 1 是寫入模式\n輸入 2 是測驗模式\n")
        
        if a == '1':
            write.main()
            
        elif a == '2':
            quiz.main()

if __name__ == "__main__":
    main()