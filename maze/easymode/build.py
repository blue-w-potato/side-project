import tkinter as tk
from random import choice

class block( tk.Label ):
    
    # 絕對位置
    x = 0
    y = 0
    # 是不是牆壁
    wall = True
    # 是不是終點
    end = False
    # 在建立的時候有沒有走訪過
    visited = False
    
class build():
    
    graph = []
    
    def init( self, ui, size, blocksize ):
        self.graph = []
        for i in range( size ):
            self.graph.append([])
            for j in range( size ):
                self.graph[-1].append(block( ui, text = "　", bg = "green", font =  ("MingLiU", 10 ) ) )
                self.graph[-1][-1].x = j*blocksize
                self.graph[-1][-1].y = i*blocksize
        self.graph[-1][-1].end = True

    def dfs( self, x, y, size ):
        if x==y==0:
            self.graph[x][y].config( text = "　", bg = "white",font =  ("MingLiU", 10 ) )
        else:
            self.graph[x][y].config( text = "　", bg = "white",font =  ("MingLiU", 10 ) )
        self.graph[x][y].wall = False
        self.graph[x][y].visited = True
        if self.graph[x][y].end:
            self.graph[x][y].config( text = "　", bg = "red",font =  ("MingLiU", 10 ) )
            return
        next = []
        if x>0:next.append( (x-1,y,size) )
        if x<size-1:next.append( (x+1,y,size) )
        if y>0:next.append( (x,y-1,size) )
        if y<size-1:next.append( (x,y+1,size) )
        for i,j,size in next:
            if self.graph[i][j].visited:
                next.remove( (i,j,size) )
        
        if len(next)>1:
            no = choice(next)
            self.graph[no[0]][no[1]].visited = True
            next.remove( no )
        for i in next:
            if self.graph[i[0]][i[1]].visited:
                continue
            self.dfs(*i)