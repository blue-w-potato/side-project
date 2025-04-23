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
        self.DecimalValue: str = str(num)     
        # Least significant bit first
        self._bits: list[bool] = [bit == '1' for bit in self.BinaryValue[::-1]]
        
    
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
        