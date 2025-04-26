class BinInt:
    """
    Binary integer type that maintains both binary and decimal representations and supports addition.
    """
    def __init__(self, num:int):
        """
        Initialize from a decimal number.

        Args:
            num (int): The decimal number to convert into binary.
        """
        
        # Binary string without '0b' prefix
        self.BinaryValue: str = bin(num)[2:]  
        # Store decimal as string
        self.DecimalValue: int = num  
        # Least significant bit first
        self._bits: list[bool] = [bit == '1' for bit in self.BinaryValue[::-1]]
        
    def __lt__(self, num2:'BinInt') -> bool:
        return self.DecimalValue < num2.DecimalValue
    
    def __gt__(self, num2:'BinInt') -> bool:
        return self.DecimalValue > num2.DecimalValue
    
    def __eq__(self, num2:'BinInt') -> bool:
        return self.DecimalValue == num2.DecimalValue
    
    def __ne__(self, num2:'BinInt') -> bool:
        return self.DecimalValue != num2.DecimalValue
    
    def __ge__(self, num2:'BinInt') -> bool:
        return self > num2 or self == num2
    
    def __le__(self, num2:'BinInt') -> bool:
        return self < num2 or self == num2
    
    
    def add(self, num2:'BinInt') -> None:
        """Add another BinInt to this one in-place.

        Args:
            num2 (BinInt): The binary integer to add.

        Returns:
            None: Updates this object's BinaryValue and DecimalValue in place.

        Example:
            >>> a = BinInt(11)  # 1011
            >>> b = BinInt(5)   #  101
            >>> a.add(b)
            >>> print(a.BinaryValue)
            '10000'
        """
            
        # addition result
        result = []
        
        # if it triggers carry
        carry = False
        
        # get lists and length
        A = self._bits
        A_length = len(A)
        
        B = num2._bits
        B_length = len(B)
        
        # make two lengths same and get the max length
        
        max_length = A_length # assume of A is longer than B
        if A_length > B_length:
            B += [False for i in range(A_length - B_length)]
            
        elif A_length < B_length:
            A += [False for i in range(B_length - A_length)]
            max_length = B_length
        
        # start computing
        for i in range(max_length):
            # get values
            a = A[i]
            b = B[i]
            
            # compute the result of the position
            r = a ^ b ^ carry
            
            # compute if it triggers carry
            carry = ( (a and b) or (a and carry) or (b and carry))
            
            # append the result of the position
            result.append(r)
        
        # if it didn't stop carrying
        if carry:
            result.append(True)
        
        # update self's properties
        self._bits = result
        self.BinaryValue = ''.join( ['1' if i else '0' for i in result[::-1]] )
        self.DecimalValue = str(int('0b'+self.BinaryValue, base=2))
    
    def subtract(self, num2:'BinInt') -> None:

        # Ensure num2 is <= self for valid subtraction
        if self < num2:
            return None
        
        # subtract result
        result = []
        
        # if it triggers regrouping
        regrouping = False
        
        # get lists and length
        A = self._bits
        A_length = len(A)
        
        B = num2._bits
        B_length = len(B)
        
        # make two lengths same and get the max length
        
        max_length = A_length # assume of A is longer than B
        if A_length > B_length:
            B += [False for i in range(A_length - B_length)]
            
        elif A_length < B_length:
            A += [False for i in range(B_length - A_length)]
            max_length = B_length
        
        # start computing
        for i in range(max_length):
            # get values
            a = A[i]
            b = B[i]
            
            r = a ^ b ^ regrouping
            regrouping = ((not a and (b or regrouping)) or (regrouping and b))

            result.append(r)

        while not result[0]:
            result.pop()
            if not result:
                result = [False]
                break

        self._bits = result
        self.BinaryValue = ''.join( ['1' if i else '0' for i in result[::-1]] )
        self.DecimalValue = str(int('0b'+self.BinaryValue, base=2))
    
