import tkinter as tk
def main( screen ):
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\title\\Category.txt", mode = "r", encoding = "utf-8" ) as txt:
        title_1 = txt.readline()
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\title\\Canva_type.txt", mode = "r", encoding = "utf-8" ) as txt:
        title_2 = txt.readline()

    title = tk.Label(
                    screen,
                    text = title_1+"\t"+title_2,
                    font = ('PMingLiU',20,'bold'),
                    bg = "white"
                    )
    title.place( x = 600, y = 200 )