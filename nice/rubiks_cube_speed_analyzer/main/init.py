import get_original_data
import Canva_type_Buttons
import matplotlib.pyplot as plt
import WB_trainning

def main( retrainning, screen, table ):
    if  retrainning:
        WB_trainning.Trainning()
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzer\\main\\title\\Category.txt", mode = "wt", encoding = "utf-8" ) as txt:
        print( get_original_data.get_Categorys()[0], end = "", file=txt )
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzer\\main\\title\\Canva_type.txt", mode = "wt", encoding = "utf-8" ) as txt:
        print( Canva_type_Buttons.main.Categorys_name[0], end = "", file=txt )
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzer\\main\\title\\Canva_type.txt", mode = "wt", encoding = "utf-8" ) as txt:
        print( Canva_type_Buttons.main.Categorys_name[0], end = "", file = txt )
    data = get_original_data.get_data( get_original_data.get_Categorys()[0] )
    Canva_type_Buttons.main.Categorys[0]( data[0], data[1] )
    plt.grid( True )
    import datas
    datas.show_datas( screen, data[1] )
    plt.draw()
    # 繪製圖表

    # 標題
    import title
    title.main( screen )

    # 選擇模式
    import Category_Buttons
    Canva_type_Buttons.main.main( get_original_data.get_Categorys()[0], screen, [ [], [] ], table)
    Category_Buttons.main.main( screen, table )
