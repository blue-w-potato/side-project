import tkinter as tk

class UI(tk.Tk):
    title = "maze(Hard_Mode)"
    
    def show_all( self, graph ):
        for i in graph:
            for j in i:
                j.place( x = j.x, y = j.y )
    