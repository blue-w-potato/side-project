while True:
    money = int(input("輸入價格\n"))
    time_long = int(input("輸入估計使用年限\n"))
    died_money = int(input("輸入估計殘值\n"))
    input(f"每年折舊金額：{(money-died_money)//time_long}")