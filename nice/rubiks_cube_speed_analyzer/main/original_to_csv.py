def main():
    from os import listdir
    
    # 取得目錄中的檔案清單
    data_name = listdir( "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzer\\main\\original_data" )

    # 讀取原始資料
    original_data = open( encoding = "utf-8", mode = "r", file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzer\\main\\original_data\\" + data_name[0] )
    data = []
    while 1:
        try:
            line = original_data.readline()
            if line == "":
                break
            data.append( line[:-4] )
        except EOFError:
            break
    original_data.close()
    
    # 處理資料
    csv_data = []
    for i in data:
        if ";" in list(i):
            csv_data.append( ",".join( i.split( ";" ) ) )
        else:
            csv_data.append( i )
            
    # 存檔
    new_data = open( encoding = "utf-8", mode = "wt", file = "C:\\Users\\88690\\Desktop\\side-project\\rubiks_cube_speed_analyzer\\main\\csv_data\\original_data.csv" )
    for i in csv_data:
        print( i, file = new_data )
    new_data.close()
