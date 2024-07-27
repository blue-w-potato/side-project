import get_original_data
import matplotlib.pyplot as plt
import tkinter as tk
import title

class main:
    Categorys = get_original_data.get_Categorys()
    fig_types_button = []
    
    def button_command(selected_category, table, screen):
            data = get_original_data.get_data(selected_category)  # (times -> list, speeds -> list)
            with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\title\\Category.txt", mode = "wt", encoding = "utf-8" ) as txt:
                print( selected_category, end = "", file = txt )
            with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\title\\Canva_type.txt", mode = "wt", encoding = "utf-8" ) as txt:
                print( "--------- ", end = "", file = txt )
            title.main( screen )
            import Canva_type_Buttons
            Canva_type_Buttons.main.main( screen, data, table )
            
            
    def main( screen, table):
        for i, category in enumerate(main.Categorys):
            main.fig_types_button.append(tk.Button(
                screen,
                text=category,
                bg="lightblue",
                width=10,
                command=lambda cat=category: main.button_command(cat, table, screen)
            ))
            main.fig_types_button[-1].place(x=300, y=0 + i * 30)
