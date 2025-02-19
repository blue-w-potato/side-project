def main():
    while True:
        a = input("輸入 1 是寫入模式\n輸入 2 是測驗模式")
        if a == '1':
            write()
        elif a == '2':
            quiz()
        else:
            print('洗勒靠')

def quiz():
    pass

def write():
    def settings():
        a = False
        while True:
            a = input("單字還是片語")
            if a == '單字' or '片語':
                break
            else:
                print('洗勒靠')
        
        if a == '單字':
            b = input("要詞性的話輸入 1 ") == '1'
        else:
            b = False

        return [a,b]
    
    setting = settings()
