import matplotlib.pyplot as plt
import tkinter as tk
import title

class main:
    Categorys = [ plt.scatter, plt.plot ]
    Categorys_name = [ "散布圖", "折線圖" ]
    def main( screen, data, table ):
        
        Buttons = []
        for i in range( len(main.Categorys) ):
            Buttons.append( tk.Button(screen,
                                      text = main.Categorys_name[i],
                                      bg = "red",
                                      width=10,
                                      command=lambda cat = i: main.button_command(screen, cat, data, table)
                                      ) ) 
            Buttons[-1].place( x=600, y=0 + i * 30 )
    
    
    def button_command( screen, Cat, data, table ):
        plt.clf()
        main.Categorys[Cat](data[0],data[1])
        with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\title\\Canva_type.txt", mode = "wt", encoding = "utf-8" ) as txt:
            print( main.Categorys_name[Cat], end = "", file = txt )
        plt.xlabel("Speed")
        plt.ylabel("Time")
        plt.grid( True )
        table.draw()
        title.main( screen )
    
