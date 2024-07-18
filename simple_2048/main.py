import pygame
import sys
import random

pygame.init()

WIDTH, HEIGHT = 400, 400
GRID_SIZE = 4
CELL_SIZE = WIDTH // GRID_SIZE

screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("2048")

BACKGROUND_COLOR = (187, 173, 160)
EMPTY_CELL_COLOR = (205, 193, 180)
FONT = pygame.font.Font(None, 36)

TILE_COLORS = {
    0: (205, 193, 180),    # 空格顏色
    2: (238, 228, 218),
    4: (237, 224, 200),
    8: (242, 177, 121),
    16: (245, 149, 99),
    32: (246, 124, 95),
    64: (246, 94, 59),
    128: (237, 207, 114),
    256: (237, 204, 97),
    512: (237, 200, 80),
    1024: (237, 197, 63),
    2048: (237, 194, 46),
    4096: (0, 0, 0),       # 黑色
    8192: (0, 0, 0),
    16384: (0, 0, 0),
    32768: (0, 0, 0)
}

# 文字顏色
TEXT_COLOR = {
    2: (119, 110, 101),    # 深灰色用於低數值tile
    4: (119, 110, 101),
    8: (249, 246, 242),    # 白色用於高數值tile
    16: (249, 246, 242),
    32: (249, 246, 242),
    64: (249, 246, 242),
    128: (249, 246, 242),
    256: (249, 246, 242),
    512: (249, 246, 242),
    1024: (249, 246, 242),
    2048: (249, 246, 242),
    4096: (249, 246, 242),    # 對於黑色背景，使用白色文字
    8192: (249, 246, 242),
    16384: (249, 246, 242),
    32768: (249, 246, 242)
}


class Tile:
    def __init__(self, value, row, col):
        self.value = value
        self.row = row
        self.col = col
        self.target_row = row
        self.target_col = col

    def move(self, target_row, target_col):
        self.target_row = target_row
        self.target_col = target_col

    def update(self):
        if self.row < self.target_row:
            self.row += 0.2
        elif self.row > self.target_row:
            self.row -= 0.2
        if self.col < self.target_col:
            self.col += 0.2
        elif self.col > self.target_col:
            self.col -= 0.2

        # 四捨五入以避免浮點誤差
        self.row = round(self.row, 1)
        self.col = round(self.col, 1)

    def is_moving(self):
        return self.row != self.target_row or self.col != self.target_col


def get_font_size(value):
    if value < 128:
        return 36
    elif value < 1024:
        return 32
    else:
        return 24
    
def draw_board(board):
    screen.fill(BACKGROUND_COLOR)
    for y in range(GRID_SIZE):
        for x in range(GRID_SIZE):
            value = board[y][x]
            cell_rect = pygame.Rect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE)
            pygame.draw.rect(screen, TILE_COLORS[value], cell_rect, border_radius=8)
            if value != 0:
                font_size = get_font_size(value)
                font = pygame.font.Font(None, font_size)
                text = font.render(str(value), True, TEXT_COLOR.get(value, (249, 246, 242)))
                text_rect = text.get_rect(center=cell_rect.center)
                screen.blit(text, text_rect)
    pygame.display.flip()

def move(board, direction):
    # 複製板子,用於檢查是否有變化
    old_board = [row[:] for row in board]
    
    if direction == 'left':
        board = [merge(row) for row in board]
    elif direction == 'right':
        board = [merge(row[::-1])[::-1] for row in board]
    elif direction == 'up':
        board = transpose(board)
        board = [merge(row) for row in board]
        board = transpose(board)
    elif direction == 'down':
        board = transpose(board)
        board = [merge(row[::-1])[::-1] for row in board]
        board = transpose(board)

    if board != old_board:
        add_new_tile(board)
    
    return board

def merge(row):
    # 移除零
    row = [i for i in row if i != 0]
    
    # 合併相同的數字
    for i in range(len(row) - 1):
        if row[i] == row[i + 1]:
            row[i] *= 2
            row[i + 1] = 0
    
    # 再次移除零並填充
    row = [i for i in row if i != 0]
    row += [0] * (4 - len(row))
    
    return row

def transpose(board):
    return [list(row) for row in zip(*board)]

def add_new_tile(board):
    empty = [(i, j) for i in range(4) for j in range(4) if board[i][j] == 0]
    if empty:
        i, j = random.choice(empty)
        board[i][j] = 2 if random.random() < 0.9 else 4

def main():
    board = [[0 for _ in range(4)] for _ in range(4)]
    add_new_tile(board)
    add_new_tile(board)

    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT:
                    board = move(board, 'left')
                elif event.key == pygame.K_RIGHT:
                    board = move(board, 'right')
                elif event.key == pygame.K_UP:
                    board = move(board, 'up')
                elif event.key == pygame.K_DOWN:
                    board = move(board, 'down')
        
        draw_board(board)
        pygame.display.flip()

    pygame.quit()

if __name__ == "__main__":
    main()