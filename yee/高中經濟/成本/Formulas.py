import pandas as pd

MC = lambda x: (x-100)**2/1 + 20

def TVC(MC:pd.Series, Q:pd.Series) -> pd.Series:
    '''
    TVC[n] = MC[0] + MC[1] + ... + MC[n]
    '''

    result = pd.Series( [ 0 for i in range(Q.size) ] )
    
    for i in range(1, Q.size):
        result[i] = result[i-1] + MC[i]
    
    return result

def AVC(TVC:pd.Series, Q:pd.Series) -> pd.Series:
    ''''
    AVC = TVC / Q
    '''
    result = TVC / Q
    
    return result

def AFC(TFC:int, Q:pd.Series) -> pd.Series:
    '''
    AFC = TFC / Q
    '''
    result = TFC / Q
    
    return result

def TC(TVC:pd.Series, TFC:int):
    '''
    TFC = TVC + TFC 
    '''
    result = TVC + TFC
    
    return result

def AC(TC:pd.Series, Q:pd.Series) -> pd.Series:
    '''
    AC = TC / Q
    '''
    result = TC / Q
    
    return result