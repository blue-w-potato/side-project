import tkinter as tk
from random import choice, shuffle

class block:
    def __init__( self, x:int, y:int, udlr:list, wall:bool, end:bool, visited:bool ):
        self.x = x # 在介面中的絕對位置
        self.y = y
        self.udlr = udlr # 相對位置
        self.wall = wall # 是不是牆壁
        self.end = end # 是不是終點
        self.visited = visited


SIZE = 50 #  迷宮大小
BLOCK_SIZE = 20 # 方塊大小 

# UI
screen = tk.Tk()
screen.geometry( str( BLOCK_SIZE*(SIZE+3) ) + "x" + str( BLOCK_SIZE*(SIZE) ) )
screen.title = "MAZE"
graph = []
player, player_x, player_y = None, None, None
end_messnge = None

# 生成地圖
def dfs( x, y ):
    global graph
    graph[x][y].wall = False
    graph[x][y].visited = True
    if graph[x][y].end:
        return
    next = []
    if x>0:next.append( (x-1,y) )
    if x<SIZE-1:next.append( (x+1,y) )
    if y>0:next.append( (x,y-1) )
    if y<SIZE-1:next.append( (x,y+1) )
    for i,j in next:
        if graph[i][j].visited:
            next.remove( (i,j) )
    
    if len(next)>1:
        no = choice(next)
        graph[no[0]][no[1]].visited = True
        next.remove( no )
    for i in next:
        if graph[i[0]][i[1]].visited:
            continue
        dfs(*i)

Lables = []
def draw():
        global  Lables, SIZE, screen, graph
        for i in range( SIZE ):
            for j in range( SIZE ):
                root = graph[i][j]
                Lables.append( tk.Label(screen,
                        text = "起" if root.x == root.y == 0 else ("牆" if root.wall else "路"),
                        bg = "orange" if root.wall else "lightgreen"
                        ) )
                Lables[-1].place( x = root.x, y = root.y )

def bulid_map():
    global screen, graph
    graph = []
    for i in range( SIZE ):
        graph.append( [] )
        for j in range( SIZE ):
            l = graph[-1][-1] if j>0 else None
            u = graph[-2][j] if i>0 else None
            x = i*BLOCK_SIZE
            y = j*BLOCK_SIZE
            graph[-1].append( block(
                x = x,
                y = y,
                udlr = [u, None, l, None,],
                wall = True,
                end = False,
                visited = False
            ) )
            if i >0:
                new_udlr = graph[-2][j].udlr.copy()
                new_udlr[1] = graph[-1][-1]
                graph[-2][j].udlr = new_udlr
            if j>0:
                new_udlr = graph[-1][-2].udlr.copy()
                new_udlr[3] = graph[-1][-1]
                graph[-1][-2].udlr = new_udlr
    else:
        graph[-1][-1].end = True
    
    dfs( 0, 0 )
    root = graph[0][0]
    graph[-1][-1].wall = False
    

    visited = []
    Lables = []
    
    draw()

    Lables.append( tk.Label(screen,
                text = "終",
                bg = "lightgreen"
                ) )
    Lables[-1].place( x = BLOCK_SIZE*(SIZE-1), y = BLOCK_SIZE*(SIZE-1) )
    global player, player_x, player_y
    # 角色
    player = tk.Label( screen, text = ": )", bg = "lightblue" )
    player_x = 0
    player_y = 0
    player.place( x=player_x*BLOCK_SIZE, y = player_y*BLOCK_SIZE )
    global end_messnge
    end_messnge = tk.Label( screen, text = "你贏了", bg = "yellow", font=('Arial',50) )
    end_messnge.forget()

def replay():
    global end_messnge
    end_messnge.forget()
    bulid_map()

    
bulid_map()
bulid = tk.Button(screen, text="重新生成", bg = "yellow", command=replay)
bulid.place( x = BLOCK_SIZE*(SIZE), y = BLOCK_SIZE*(SIZE-1) )
end_messnge.pack()
end_messnge.forget()

def end():
    global end_messnge
    end_messnge.pack()

def go( key ):
    global player_x, player_y
    turn = key.keycode
    newx = 0
    newy = 0
    if turn == 37: newx = player_x - 1; newy = player_y+0
    if turn == 39: newx = player_x + 1; newy = player_y+0
    if turn == 38: newy = player_y - 1; newx = player_x+0
    if turn == 40: newy =player_y + 1; newx = player_x+0
    if newx<0 or newx>SIZE-1:
        print("撞牆")
        return
    if newy<0 or newy>SIZE-1:
        print("撞牆")
        return
    if graph[newx][newy].wall:
        print("撞牆")
        return
    player_x, player_y = newx, newy
    player.place( x=player_x*BLOCK_SIZE, y = player_y*BLOCK_SIZE )
    print( player_x, player_y )
    if player_x+1 == player_y+1 == SIZE:
        end()
    return
player.focus_set()
player.bind( "<Key>", go )

screen.mainloop()