import pandas as pd
import matplotlib.pyplot as plt
read_data = None
def get_Categorys() -> list:
    global read_data
    read_data
    read_data = pd.read_csv( "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\csv_data\\original_data.csv" )
    read_data = pd.DataFrame( read_data, index = [ i for i in range( 1, read_data.size+1 ) ] )
    Categorys = pd.Series(list(set(list(read_data[ "Category" ]))))
    return sorted(list(Categorys[[ not i for i in list(Categorys.isnull()) ]]))

def get_data( Category ) -> tuple:
    import matplotlib.pyplot as plt
    data = list(read_data[ "Time(millis)" ][[read_data[ "Category" ][i] == Category for i in range( 1, 1+read_data["Category"].size )]])
    data = pd.Series( data, index = [ i for i in range(len(data)) ] )
    for i in range( data.size ):
        data[i] /= 1000
    return ([ i for i in range( 1, 1+data.size ) ], data)
    

