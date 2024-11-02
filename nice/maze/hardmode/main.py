import build
import tkinter as tk
import sys
import time

class main:
    SIZE = 50
    BLOCK_SIZE = 100
    SHOW_SIZE = 5
    UI_SIZE = BLOCK_SIZE*SHOW_SIZE
    test_mode = False
    Input = input( "迷宮(困難模式)\n作者：blue_w_potato\n按下enter開始" )
    try:
        Input = list(map(int, Input))
        if sum(Input) == 22:
            test_mode = True
    except:
        pass
    ui = tk.Tk()
    ui.title = "maze(Hard_Mode)"
    ui.configure(background='black')
    player = tk.Label( ui, text = ":))", font=('Arial',80), bg = "lightblue" )
    player.place( x=BLOCK_SIZE*2, y=BLOCK_SIZE*2 )
    player_x = 0
    player_y = 0
    graph = build.build()
    graph.init( ui = ui, size = SIZE )
    showing = []
        
    def main():
        
        main.ui.geometry( f"{main.UI_SIZE}x{main.UI_SIZE}" )
        while main.graph.graph[-1][-1].wall:
            main.graph.init( ui = main.ui, size = main.SIZE )
            main.graph.dfs( x = 0, y = 0, size = main.SIZE )
        main.running()
        main.ui.mainloop()
    
    def go( key ):
        print(key.keysym, key.keycode)
        turn = key.keycode
        newx = 0
        newy = 0
        if turn == 37: newx = main.player_x - 1; newy = main.player_y+0
        if turn == 39: newx = main.player_x + 1; newy = main.player_y+0
        if turn == 38: newy = main.player_y - 1; newx = main.player_x+0
        if turn == 40: newy =main.player_y + 1; newx = main.player_x+0
        if newx<0 or newx>main.SIZE-1:
            print("外面沒有海")
            return
        if newy<0 or newy>main.SIZE-1:
            print("你追求自由嗎")
            return
        if main.graph.graph[newy][newx].wall and not main.test_mode:
            print("撞牆")
            return
        main.clean()
        main.player_x, main.player_y = newx+0, newy+0
        main.show()
        main.player.lift()
        if main.player_x+1 == main.player_y+1 == main.SIZE:
            main.end()
        return
    
    def running():
        main.show()
        main.player.lift()
        main.player.focus_set()
        main.player.bind( "<Key>", main.go )
        
    def end():
        messange = tk.StringVar()
        end_messange = tk.Label(main.ui,textvariable=messange, font=('Arial',35), bg = "yellow")
        for i in range(3, 0, -1):
            messange.set(f"你贏了，{i}秒後自動關閉")
            print(messange.get())
            end_messange.update()
            end_messange.pack()
            main.ui.update()
            time.sleep(1)
        sys.exit()
    
    def clean():
        
        for i in main.showing:
            for j in i:
                if not(j is None):
                    j.forget()
                
    
    def show_all():
        for x,i in enumerate(main.graph.graph):
            for y,j in enumerate(i):
                j.place( x = x*main.BLOCK_SIZE, y = y*main.BLOCK_SIZE )
    
    def show():
        main.showing = []
        for i in range( main.player_x-2, main.player_x+3 ):
            if i<0 or i>=main.SIZE:
                main.showing.append([ tk.Label( main.ui, text = "　", bg = "green", font =  ("MingLiU", 80 ) ) for i in range( 5 ) ])
            else:
                main.showing.append([])
                for j in range( main.player_y-2, main.player_y+3 ):
                    if j<0 or j>=main.SIZE:
                        main.showing[-1].append( tk.Label( main.ui, text = "　", bg = "green", font =  ("MingLiU", 80 ) ) )
                    else:
                        main.showing[-1].append( main.graph.graph[j][i] )
                        
        for x,i in enumerate(main.showing):
            for y,j in enumerate(i):
                if not (j is None):
                    j.place( x = x*main.BLOCK_SIZE, y = y*main.BLOCK_SIZE )
                    j.lift()

if __name__ == "__main__":
    main.main()