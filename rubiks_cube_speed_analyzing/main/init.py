import get_original_data
import Canva_type_Buttons
import matplotlib.pyplot as plt

def main( screen, table ):
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\title\\Category.txt", mode = "wt", encoding = "utf-8" ) as txt:
        print( get_original_data.get_Categorys()[0], end = "", file=txt )
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\title\\Canva_type.txt", mode = "wt", encoding = "utf-8" ) as txt:
        print( Canva_type_Buttons.main.Categorys_name[0], end = "", file=txt )
    data = get_original_data.get_data( get_original_data.get_Categorys()[0] )
    Canva_type_Buttons.main.Categorys[0]( data[0], data[1] )
    plt.grid( True )
    plt.draw()