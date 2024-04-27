# 遊戲介紹
print( "2048 cmd 版" )
print( "作者：Blue-W-Potato" )
print( "" )
print( "操作說明：" )
print( "" )
print( "輸入 \"8\" 是 \"往上\"" )
print( "　　 \"4\" 是 \"往左\"" )
print( "　　 \"2\" 是 \"往下\"" )
print( "　　 \"6\" 是 \"往右\"" )
print( "" )
print( "其餘輸入皆無效" )
print( "" )
print( "規則說明：" )
print( "" )
print( "規則大致上跟一般的 2048 相同，有玩過都知道" )
print( "" )
input( "準備好了就按下 Enter 開始遊戲\n" )

# 主迴圈
import random
import numpy as np

running = True # 主迴圈是否正常運行
playing = True # 是否在遊戲中

class main:
    # 4x4 的二維陣列
    main_array = np.zeros((4,4), dtype=int)
    main_array_copy = main_array.copy()
    changed = False
    
    def check():
        # 檢查
        if main.changed:
            main.new_number()
        else:
            main.main_array = main.main_array_copy.copy()
            print( "\n動不了\n" )
    
    def new_number():
        # 新增數字
        xys = [ [ i, j ] for i in range(4) for j in range(4) ]
        x1, y1 = random.choice(xys)
        no = False
        while main.main_array[x1][y1] != 0:
            xys.remove([x1,y1])
            if len(xys) == 0:
                no = True
                break
            x1, y1 = random.choice(xys)
        if not no:
            main.main_array[x1][y1] = random.choice( [ 2 for i in range(9) ] + [4] )
        
    def go_left():
        # 備份 main_array ， 用來檢查能不能動
        main.main_array_copy = main.main_array.copy()
        
        # 移動
        for i in range(4):
            for k in range(1, 4):
                now = k
                while main.main_array[i][now-1] == 0 and main.main_array[i][now] != 0:
                    main.main_array[i][now-1], main.main_array[i][now] = main.main_array[i][now], main.main_array[i][now-1]
                    main.changed = True
                    now -= 1
                    if now <= 0:
                        break
    
        # 合併
        for k in range(4):
            for i in range(1, 4):
                if main.main_array[k][i-1] == main.main_array[k][i] != 0:
                    main.main_array[k][i] *= 2
                    main.main_array[k][i-1] = 0
                    main.changed = True
                    # 移動
                    for ia in range(4):
                        for ka in range(1, 4):
                            now = ka
                            while main.main_array[ia][now-1] == 0 and main.main_array[ia][now] != 0:
                                main.main_array[ia][now-1], main.main_array[ia][now] = main.main_array[ia][now], main.main_array[ia][now-1]
                                main.changed = True
                                now -= 1
                                if now <= 0:
                                    break
        # 檢查
        main.check()
    
    def go_right(): 
        # 備份 main_array ， 用來檢查能不能動
        main.main_array_copy = main.main_array.copy()       
        
        # 移動
        for i in range(4):
            for k in range(2, -1, -1):
                now = k
                while main.main_array[i][now+1] == 0 and main.main_array[i][now] != 0:
                    main.main_array[i][now+1], main.main_array[i][now] = main.main_array[i][now], main.main_array[i][now+1]
                    main.changed = True
                    now += 1
                    if now >= 3:
                        break
    
        # 合併
        for k in range(4):
            for i in range(2, -1, -1):
                if main.main_array[k][i+1] == main.main_array[k][i] != 0:
                    main.main_array[k][i] *= 2
                    main.main_array[k][i+1] = 0
                    main.changed = True
                    # 移動
                    for ia in range(4):
                        for ka in range(2, -1, -1):
                            now = ka
                            while main.main_array[ia][now+1] == 0 and main.main_array[ia][now] != 0:
                                main.main_array[ia][now+1], main.main_array[ia][now] = main.main_array[ia][now], main.main_array[ia][now+1]
                                main.changed = True
                                now += 1
                                if now >= 3:
                                    break
        # 檢查
        main.check()
    
    def go_up():
        # 備份 main_array ， 用來檢查能不能動
        main.main_array_copy = main.main_array.copy()
        
        # 移動
        for i in range(4):
            for k in range(1, 4):
                now = k
                while main.main_array[now-1][i] == 0 and main.main_array[now][i] != 0:
                    main.main_array[now-1][i], main.main_array[now][i] = main.main_array[now][i], main.main_array[now-1][i]
                    main.changed = True
                    now -= 1
                    if now <= 0:
                        break
    
        # 合併
        for k in range(4):
            for i in range(1, 4):
                if main.main_array[i-1][k] == main.main_array[i][k] != 0:
                    main.main_array[i][k] *= 2
                    main.main_array[i-1][k] = 0
                    main.changed = True
                    # 移動
                    for ia in range(4):
                        for ka in range(1, 4):
                            now = ka
                            while main.main_array[now-1][ia] == 0 and main.main_array[now][ia] != 0:
                                main.main_array[now-1][ia], main.main_array[now][ia] = main.main_array[now][ia], main.main_array[now-1][ia]
                                main.changed = True
                                now -= 1
                                if now <= 0:
                                    break
        # 檢查
        main.check()
    
    def go_down():
        # 備份 main_array ， 用來檢查能不能動
        main.main_array_copy = main.main_array.copy()
        
        # 移動
        for i in range(4):
            for k in range(2, -1, -1):
                now = k
                while main.main_array[now+1][i] == 0 and main.main_array[now][i] != 0:
                    main.main_array[now+1][i], main.main_array[now][i] = main.main_array[now][i], main.main_array[now+1][i]
                    main.changed = True
                    now += 1
                    if now >= 3:
                        break
    
        # 合併
        for k in range(4):
            for i in range(2, -1, -1):
                if main.main_array[i+1][k] == main.main_array[i][k] != 0:
                    main.main_array[i][k] *= 2
                    main.main_array[i+1][k] = 0
                    main.changed = True
                    # 移動
                    for ia in range(4):
                        for ka in range(2, -1, -1):
                            now = ka
                            while main.main_array[now+1][ia] == 0 and main.main_array[now][ia] != 0:
                                main.main_array[now+1][ia], main.main_array[now][ia] = main.main_array[now][ia], main.main_array[now+1][ia]
                                main.changed = True
                                now += 1
                                if now >= 3:
                                    break
        # 檢查
        main.check()
    
    def output():
        print()
        view = [ [ ' ' if i == 0 else i  for i in j ] for j in main.main_array ]
        for i in view:
            print( '|', end = '' )
            for j in i:
                print(f'{j:^5}', end = '|')
            print()
        print()
            
    def game_over():
        # 試能不能往左或往右
        for i in range(4):
            for j in range(1, 4):
                if main.main_array[i][j-1] == main.main_array[i][j] or 0 in (main.main_array[i][j-1] , main.main_array[i][j]):
                    return False
        
        # 試能不能往上或往下
        for i in range(1, 4):
            for j in range(4):
                if main.main_array[i-1][j] == main.main_array[i][j] or 0 in (main.main_array[i][j-1] , main.main_array[i][j]):
                    return False
        
        # 都不能動
        return True

while running:
    # 初始化
    main.main_array = np.zeros((4,4), dtype=int)
    init_x1 = random.choice(( 0, 1, 2, 3 ))
    init_y1 = random.choice(( 0, 1, 2, 3 ))
    main.main_array[init_x1][init_y1] = random.choice( [ 2 for i in range(9) ] + [4] )
    
    init_x2 = random.choice(( 0, 1, 2, 3 ))
    init_y2 = random.choice(( 0, 1, 2, 3 ))
    while ( init_x1, init_y1 ) == ( init_x2, init_y2 ):
        init_x2 = random.choice(( 0, 1, 2, 3 ))
        init_y2 = random.choice(( 0, 1, 2, 3 ))
    main.main_array[init_x2][init_y2] = random.choice( [ 2 for i in range(9) ] + [4] )
    
    main.main_array_copy = main.main_array.copy()
    main.changed = False
    # 顯示初始陣列
    main.output()
    
    while playing:
        
        # 還沒變動陣列
        main.changed = False
        
        # 使用者下指令
        move = input()
        
        # 直接結束
        if move == "216026":
            print("\n你發現了彩蛋")
            print("輸入這個會直接結束，因為我要測試讓遊戲重新開始的部分")
            playing = False
            break
        
        # 瑪卡巴卡
        if move == "瑪卡巴卡":
            print("\n瑪卡·巴卡\n阿卡，哇卡\n米卡，瑪卡，呣\n瑪卡·巴卡，\n阿巴，雅卡\n伊卡，阿卡，噢\n哈姆，達姆，阿卡嗙\n咿，呀，呦\n瑪卡·巴卡，阿卡，哇卡\n米卡，瑪卡，呣\n")
            print("\n你發現了彩蛋\n")
        
        # 如果不照規則亂輸入
        if not( move in "8426" ) or not move:
            print( "操作說明：" )
            print( "" )
            print( "輸入 \"8\" 是 \"往上\"" )
            print( "　　 \"4\" 是 \"往左\"" )
            print( "　　 \"2\" 是 \"往下\"" )
            print( "　　 \"6\" 是 \"往右\"" )
            print( "" )
            print( "其餘輸入皆無效" )
            main.output()
            continue
        
        # 往上
        if move == '8':
            main.go_up()
            
        # 往左
        if move == '4':
            main.go_left()
            
        # 往右
        if move == '6':
            main.go_right()
            
        # 往下
        if move == '2':
            main.go_down()
        
            
        # 輸出
        main.output()
        
        if main.game_over():
            playing = False
            break
        
    # 卡住了
    print("\n遊戲結束\n")
    
    # 是否重玩
    again = input( "\n輸入 1 重玩，輸入 0 結束\n" )
    while not( again in '01' ):
        again = input( "\n輸入 1 重玩，輸入 0 結束\n" )
    if int(again):
        print()
        playing = True
    else:
        running = False