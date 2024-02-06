import pygame

#視窗相關設定
class screen_set():
    def __init__(self):
        self.width = 1000
        self.height = 1000
        self.size = [self.width, self.height]
        self.name = 'oh no' 
        self.back_color = [255, 255, 255]
        self.fps = 1000

screen_sets = screen_set() 

#初始化
pygame.init()

screen = pygame.display.set_mode(screen_sets.size) 
pygame.display.set_caption(screen_sets.name) 
clock = pygame.time.Clock()

# 角色圖片設定
class Player(pygame.sprite.Sprite): 
    def __init__(self, size, color, xy):
        pygame.sprite.Sprite.__init__(self) 
        self.image = pygame.Surface(size) 
        self.image.fill(color) 
        self.rect = self.image.get_rect()
        self.x = screen_sets.width/2
        self.y = screen_sets.height/2
        self.rect.center = [self.x, self.y]
all_sprites = pygame.sprite.Group()

#角色一相關設定
class Player_1_sets():
    def __init__(self):
        self.width = 40
        self.height = 40
        self.size = [self.width, self.height]
        self.color = [0, 0, 0]
        self.xy = [screen_sets.height-1, 0]

player_1_sets = Player_1_sets() 
player_1 = Player(size = player_1_sets.size, color = player_1_sets.color,  xy = player_1_sets.xy)
all_sprites.add(player_1) 

#遊戲主迴圈

running = True

while running:
    clock.tick(screen_sets.fps) 
    #取得輸入
    for event in pygame.event.get(): 
        if event.type == pygame.QUIT:
            running = False

    #更新遊戲
    a = 1/250
    h = 500
    k = 0
    if (player_1.x >= screen_sets.width) or (player_1.x < 0) or (player_1.y >= screen_sets.height) or (player_1.y < 0):
        player_1.x = 0
        player_1.y = screen_sets.height-1
    else:
        player_1.x += 1
        player_1.y = a*(player_1.x - h)**2 + k
    player_1.rect.center = [player_1.y, player_1.x][::-1]
    all_sprites.update()
    #畫面顯示
    screen.fill(screen_sets.back_color) 
    all_sprites.draw(screen) 
    pygame.display.update()

pygame.quit() 