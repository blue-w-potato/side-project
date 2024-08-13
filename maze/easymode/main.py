import UI
import build
import tkinter as tk
import sys
import time

class main:
    SIZE = 20
    BLOCK_SIZE = 30
    SHOW_SIZE = 5
    UI_SIZE = BLOCK_SIZE*SIZE
    test_mode = False
    Input = input( "迷宮(簡單模式)\n作者：blue_w_potato\n按下enter開始" )
    try:
        Input = list(map(int, Input))
        if sum(Input) == 22:
            test_mode = True
    except:
        pass
    ui = UI.UI()
    player = tk.Label( ui, text = ":))", font=('Arial',15), bg = "lightblue" )
    player_x = 0
    player_y = 0
    graph = build.build()
    graph.init( ui = ui, size = SIZE, blocksize = BLOCK_SIZE )
        
    def main():
        main.ui.geometry( f"{main.UI_SIZE}x{main.UI_SIZE}" )
        while main.graph.graph[-1][-1].wall:
            main.graph.init( ui = main.ui, size = main.SIZE, blocksize = main.BLOCK_SIZE )
            main.graph.dfs( x = 0, y = 0, size = main.SIZE )
        main.running( main.ui, main.BLOCK_SIZE, main.graph.graph )
        main.ui.mainloop()
    
    def go( key, graph ):
        print(key.keycode, key.keysym)
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
        if graph[newy][newx].wall and not main.test_mode:
            print("撞牆")
            return
        main.player_x, main.player_y = newx+0, newy+0
        main.player.place( x=main.player_x*main.BLOCK_SIZE, y = main.player_y*main.BLOCK_SIZE )
        if main.player_x+1 == main.player_y+1 == main.SIZE:
            main.end()
        return
    
    def running( ui, BLOCK_SIZE, graph ):
        main.ui.show_all(graph)
        print("="*50)
        for i in graph:
            for j in i:
                
                print(f"{j.wall:5}", end = "|")
            print()
        main.player.place( x=main.player_x*BLOCK_SIZE, y = main.player_y*BLOCK_SIZE )
        main.player.lift()
        main.player.focus_set()
        main.player.bind( "<Key>", lambda x,y=graph:main.go(x,y) )
        
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


if __name__ == "__main__":
    main.main()