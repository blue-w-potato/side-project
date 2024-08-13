import UI
import build


class main:
    SIZE = 20
    BLOCK_SIZE = 30
    SHOW_SIZE = 5
    UI_SIZE = BLOCK_SIZE*SIZE
    
    def main():
        ui = UI.UI()
        ui.geometry( f"{main.UI_SIZE}x{main.UI_SIZE}" )
        graph = build.build()
        graph.init( ui = ui, size = main.SIZE, blocksize = main.BLOCK_SIZE )
        while graph.graph[-1][-1].wall:
            graph.init( ui = ui, size = main.SIZE, blocksize = main.BLOCK_SIZE )
            graph.dfs( x = 0, y = 0, size = main.SIZE )
        ui.show_all(graph.graph)
        ui.mainloop()
    



if __name__ == "__main__":
    main.main()