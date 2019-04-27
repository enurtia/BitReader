public class BitReader
{
    //Buffer array of input bytes.
    protected byte[] buffer;
    //Integer array of bits in the current byte.
    protected int[] currByte;
    //The length of buffer array.
    protected int arrLength;
    //The current index of the byte in the buffer array.
    protected int arrCount;
    //The current index of the bit in the current byte;
    protected int count;
    
    /*
    Initializes buffer to input array and initializes appropriate variables.
    */
    public BitReader(byte[] bytes)
    {
        buffer = bytes;
        arrLength = bytes.length;
        currByte = new int[8];
    }
    
    /*
    Initializes buffer to array with only element being the input byte and initializes appropriate variables.
    */
    public BitReader(byte b)
    {
        buffer = new byte[]{b};
        arrLength = 1;
        currByte = new int[8];
    }
    
    /*
    Reads next available bit. 
    Returns -1 if there is not an available bit to read.
    */
    public synchronized int next()
    {
        if(arrCount < arrLength)
        {
            if(count == 0)
            {
                byte b = buffer[arrCount];
                String s = String.format("%8s", Integer.toBinaryString(((int)b) & 0xFF)).replace(' ', '0');
                for(int i = 0; i < 8; i++)
                {
                    currByte[i] = Integer.parseInt(s.substring(i,i+1));
                }
            }
            else if(count == 8)
            {
                count = 0;
                arrCount++;
                return next();
            }
            return currByte[count++];
        }
        
        return -1;
    }
    
    /*
    Returns true if there is an available bit to read, else returns false.
    */
    public boolean hasNext()
    {
        return !(arrCount >= arrLength || (count == 8) ? arrCount+1 >= arrLength : false);
    }
    
    /*
    Sets current byte as element n in buffer array and sets bit index to zero.
    Throws Exception if input is less than or equal to 0.
    */
    public synchronized void setByte(int n) throws IllegalArgumentException
    {
        if(n <= 0)
        {
            throw new IllegalArgumentException("Byte number can't be less than or equal to 0.");
        } 
        else
        {
            count = 0;
            arrCount = n-1;
        }
    }
    
    /*
    Retains current byte, but sets n as the bit element in current byte.
    Throws Exception if input is greater than 0 or if there is not an available 
    byte to read from.
    */
    public synchronized void setBit(int n) throws IllegalArgumentException
    {
        if(n > 8)
        {
            throw new IllegalArgumentException("Bit number can't be larger than 8.");
        }
        else if(arrCount >= arrLength)
        {
            throw new IllegalArgumentException("End of buffer has been reached.");
        }
        else
        {
            byte b = buffer[arrCount];
            String s = String.format("%8s", Integer.toBinaryString(((int)b) & 0xFF)).replace(' ', '0');
            for(int i = 0; i < 8; i++)
            {
                currByte[i] = Integer.parseInt(s.substring(i,i+1));
            }
            
            count = n-1;
        }
    }
    
    /*
    Skips by n bits.
    Throws Exception if input is less than or equal to zero
    */
    public synchronized void skip(int n) throws IllegalArgumentException
    {
        if(n <= 0)
        {
            throw new IllegalArgumentException("Skip value can't be less than or equal to 0.");
        }
        
        if(count == 0)
        {
            byte b = buffer[arrCount];
            String s = String.format("%8s", Integer.toBinaryString(((int)b) & 0xFF)).replace(' ', '0');
            for(int i = 0; i < 8; i++)
            {
                currByte[i] = Integer.parseInt(s.substring(i,i+1));
            }
        }
        
        arrCount += (count+n)/8;
        count = (n % 8 == 0) ? count : (count+n) % 8;
    }
}
