def main():
    
    # 建立csv檔
    import original_to_csv
    original_to_csv.main()
    
    # 繪製圖表
    import pandas as pd
    import matplotlib.pyplot as plt
    read_data = pd.read_csv( "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzing\\main\\csv_data\\original_data.csv" )
    read_data = pd.DataFrame( read_data, index = [ i for i in range( 1, read_data.size+1 ) ] )
    data = list(read_data[ "Time(millis)" ][[read_data[ "Category" ][i] == "單手" for i in range( 1, 1+read_data["Category"].size )]])
    data = pd.Series( data, index = [ i for i in range(len(data)) ] )
    for i in range( data.size ):
        data[i] /= 1000
    plt.scatter( [ i for i in range( 1, 1+data.size ) ], data )
    plt.show( )

if __name__ == "__main__":
    main()