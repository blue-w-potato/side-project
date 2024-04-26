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
input( "準備好了就按下 Enter 開始遊戲" )

# 主迴圈
import random

running = True # 主迴圈是否正常運行
playing = True # 是否在遊戲中

class main:
    # 4x4 的二維陣列
    main_array = [ [ 0 for i in range(4) ] for j in range(4) ]
    
    def go_up():        
        # 旋轉二維陣列
        main.main_array = [ list( map( lambda x:x[i], main.main_array ) ) for i in range(4)]
        
        # 移動
        for i in range(4):
            for k in range(1, 4):
                now = k
                while main.main_array[i][now-1] == 0:
                    main.main_array[i][now-1], main.main_array[i][now] = main.main_array[i][now], main.main_array[i][now-1]
                    now -= 1
                    if now == 0:
                        break
        
        # 合併
        for i in range(4):
            for k in range(1, 4):
                if main.main_array[i][k-1] == main.main_array[i][k]:
                    main.main_array[i][k-1] *= 2
                    main.main_array[i][k] = 0
                    # 移動
                    for ia in range(4):
                        for ka in range(1, 4):
                            now = ka
                            while main.main_array[ia][now-1] == 0:
                                main.main_array[ia][now-1], main.main_array[ia][now] = main.main_array[ia][now], main.main_array[ia][now-1]
                                now -= 1
                                if now == 0:
                                    break
        
        
        # 旋轉二維陣列
        main.main_array = [ list( map( lambda x:x[i], main.main_array ) ) for i in range(4)]
        
    def go_left():        
        
        # 移動
        for i in range(4):
            for k in range(1, 4):
                now = k
                while main.main_array[i][now-1] == 0:
                    main.main_array[i][now-1], main.main_array[i][now] = main.main_array[i][now], main.main_array[i][now-1]
                    now -= 1
                    if now == 0:
                        break
                    
        # 合併
        for i in range(4):
            for k in range(1, 4):
                if main.main_array[i][k-1] == main.main_array[i][k]:
                    main.main_array[i][k-1] *= 2
                    main.main_array[i][k] = 0
                    # 移動
                    for ia in range(4):
                        for ka in range(1, 4):
                            now = ka
                            while main.main_array[ia][now-1] == 0:
                                main.main_array[ia][now-1], main.main_array[ia][now] = main.main_array[ia][now], main.main_array[ia][now-1]
                                now -= 1
                                if now == 0:
                                    break
        
    def go_right():        
        
        # 移動
        for i in range(4):
            for k in range(2, -1, -1):
                now = k
                while main.main_array[i][now+1] == 0:
                    main.main_array[i][now+1], main.main_array[i][now] = main.main_array[i][now], main.main_array[i][now+1]
                    now += 1
                    if now == 3:
                        break
                    
        # 合併
        for i in range(4):
            for k in range(2, -1, -1):
                if main.main_array[i][k+1] == main.main_array[i][k]:
                    main.main_array[i][k+1] *= 2
                    main.main_array[i][k] = 0
                    # 移動
                    for ia in range(4):
                        for ka in range(2, -1, -1):
                            now = ka
                            while main.main_array[ia][now+1] == 0:
                                main.main_array[ia][now+1], main.main_array[ia][now] = main.main_array[ia][now], main.main_array[ia][now+1]
                                now += 1
                                if now == 3:
                                    break
                    
                
    def output():
        view = [ [ ' ' if i == 0 else i  for i in j ] for j in main.main_array ]
        for i in view:
            print( '|', end = '' )
            for j in i:
                print(f'{j:^5}', end = '|')
            print()
                
    def output():
        view = [ [ ' ' if i == 0 else i  for i in j ] for j in main.main_array ]
        for i in view:
            print( '|', end = '' )
            for j in i:
                print(f'{j:^5}', end = '|')
            print()
            
    

while running:
    # 初始化
    init_x1 = random.choice(( 0, 1, 2, 3 ))
    init_y1 = random.choice(( 0, 1, 2, 3 ))
    main.main_array[init_x1][init_y1] = random.choice( [ 2 for i in range(9) ] + [4] )
    
    init_x2 = random.choice(( 0, 1, 2, 3 ))
    init_y2 = random.choice(( 0, 1, 2, 3 ))
    while ( init_x1, init_y1 ) == ( init_x2, init_y2 ):
        init_x2 = random.choice(( 0, 1, 2, 3 ))
        init_y2 = random.choice(( 0, 1, 2, 3 ))
    main.main_array[init_x2][init_y2] = random.choice( [ 2 for i in range(9) ] + [4] )
    
    main.output()
    
    
    while playing:
        
        # 使用者下指令
        move = input()
        # 如果不照規則亂輸入
        if not( move in "8426" ):
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
            
        # 新增數字
        x1 = random.choice(( 0, 1, 2, 3 ))
        y1 = random.choice(( 0, 1, 2, 3 ))
        while main.main_array[x1][y1] != 0:
            x1 = random.choice(( 0, 1, 2, 3 ))
            y1 = random.choice(( 0, 1, 2, 3 ))
        main.main_array[x1][y1] = random.choice( [ 2 for i in range(9) ] + [4] )
            
        # 輸出
        main.output()