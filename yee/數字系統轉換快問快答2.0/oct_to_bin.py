import time
import random as r

class main:
    nums = [i for i in range(1,32767)]
    Nums = [i for i in range(1,32767) if i%5 == 0]
    
    def getTime():
        noww = list(map(int,time.ctime(time.time()).split()[3].split(':')))
        now = noww[0]*60*60+noww[1]*60+noww[2]
        
        return now
    
    def integer():
        n = r.choice(main.nums)
        
        octN = str(oct(int(n)))[2::]
        binN = str(bin(int(n)))[2::]
        
        return [octN, binN]
    

        
    def play():
        while True:
            correctCount = 0
            input('接下來會出現\n\n8進位的整數5位數~1位數\n和小數5位數~1位數\n\n要在20秒內轉2進位並輸入\n超過20秒或是答錯就結束\n\n按enter開始')
            while True:
                
                integer = main.integer()
                decimal = main.integer()
                
                k = len(decimal[1])
                while k%3 != 0:
                    decimal[1] = '0' + decimal[1]
                    k = len(decimal[1])
                while decimal[1][-1] == '0':
                    decimal[1] = decimal[1][:-1]
                while decimal[2][-1] == '0':
                    decimal[2] = decimal[2][:-1]
                    
                
                octN = integer[0] + '.' + decimal[0]
                binN = integer[1] + '.' + decimal[1]
                
                
                
                startTime = main.getTime()
                
                ans = input(octN+'\n')
                
                endTime = main.getTime()
                
                usedTime = endTime-startTime
                
                if usedTime < 20 and ans == binN:
                    correctCount += 1
                    continue
                else:
                    break
            
            if usedTime >= 20:
                print('太慢了')
            if ans != binN:
                print('答錯了\n答案是：', binN)
                
            k = input('要換模式的話輸入 yee\n或者再一次')
            
            if k == 'yee':
                return