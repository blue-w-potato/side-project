import matplotlib.pyplot as plt
import pandas as pd
import tkinter as tk
from get_original_data import get_Categorys, get_data
import numpy as np
import Canva_type_Buttons
        
def cost_function( x, y, w, b ):
    y_pred = x*w + b
    cost = ( y - y_pred )**2
    cost = cost.sum() / len( x )
    return cost


def gradient_descent( table, x, y, w, b, Learning_rate, run_iter ):
    n_cost = cost_function( x, y, w, b )
    speed = 1
    
    while 1:
        cost = n_cost + 0
        if cost < run_iter:
            print( w, b )
            return (w, b)
        
        
        wm = (2*x*( w*x - y + b )).sum() / len(x)
        bm = (2*( w*x - y + b )).sum() / len(x)

        w -= wm*Learning_rate
        b -= bm*Learning_rate
        n_cost = cost_function( x, y, w, b )
        if n_cost > cost:
            Learning_rate *= -0.5
            speed = 1
        else:
            Learning_rate *= (1.5 * speed)
            speed += 0.5
        
        print( f"{w}\t{b}\t{cost}\t{Learning_rate}\t{run_iter}" )
        
        

def plot_pred( table, x, y, w, b ):
        y_pred = w*x + b
        plt.plot(x, y_pred, color = "red")
        plt.legend()
        table.draw()

def main( Category, table ):
    
    n = get_Categorys().index( Category )
    data = get_data( Category )
    x = data[0]
    y = data[1]
    
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\WBs.txt", mode = "r", encoding = "utf-8" ) as file:
        datas = list(map(lambda x:x[:-2].split(), file.readlines()))
        w, b, Learning_rate = map(float,datas[n])
    
    run_iter =  y.sum() / y.size +3
    w, b = gradient_descent( table, x, y, w, b, Learning_rate, run_iter )
    plot_pred( table, x, y, w, b )
    
def click_to_open(table):
    
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\title\\Category.txt", mode = "r", encoding = "utf-8" ) as txt:
        Category = txt.read()
    main( Category, table )
    
def click_to_close(table):
    
    plt.clf()
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\title\\Category.txt", mode = "r", encoding = "utf-8" ) as txt:
        Category = txt.read()
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\title\\Canva_type.txt", mode = "r", encoding = "utf-8" ) as txt:
        Canva_type = txt.read()
    
    data = get_data( Category )
    Canva_type_Buttons.main.Categorys[ Canva_type_Buttons.main.Categorys_name.index( Canva_type ) ]( data[0], data[1] )
    plt.xlabel("Speed")
    plt.ylabel("Time")
    plt.grid( True )
    table.draw()

class button:
    Open = 0
    txt = "線性回歸"
    def Button_command(screen, table):
        
        with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\line_show.txt", mode = "r", encoding = "utf-8" ) as file:
            Open = int(file.read())
            
        if Open:
            with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\line_show.txt", mode = "wt", encoding = "utf-8" ) as file:
                print( "0", end = "", file = file )
            click_to_close( table )
            Open = 0
            button.txt = "線性回歸" 
        else:
            with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\line_show.txt", mode = "wt", encoding = "utf-8" ) as file:
                print( "1", end = "", file = file )
            click_to_open(  table )
            Open = 1
            button.txt = "線性回歸" 
    def set_button( screen, table ):
        with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\line_show.txt", mode = "wt", encoding = "utf-8" ) as file:
            print( "0", end = "", file = file )
        Button = tk.Button(screen,
                            text = button.txt,
                            bg = "orange",
                            command = lambda x=screen,y=table: button.Button_command(x,y))
        Button.place( x =  1000, y = 200 )