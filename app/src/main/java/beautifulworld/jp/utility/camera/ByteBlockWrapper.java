package beautifulworld.jp.utility.camera;

public class ByteBlockWrapper implements ByteArrayContainer
 {
  private ByteArrayWrapper[] wrappers;
  private int currentWrapper;
  private int currentIndex;
  private byte[] currentBuffer;

  public ByteBlockWrapper(ByteArrayWrapper[] arg)
   {
	if (arg == null) throw new IllegalArgumentException("Null argument is not accepted");  

    wrappers = arg; 
   }
 
  
  @Override
  public void write8(int address, int value){} //書き込みメソッド 
  @Override
   public void write16(int address, int value){} //書き込みメソッド 
  @Override
  public void write32(int address, int value){} //書き込みメソッド 

  public void setIndex(int address)
   {
	int arrayindex = 0;

	for(int i = 0; i < wrappers.length; i++)
	 {
	  int datalength = wrappers[i].getDataLength();
	      
	  if (address >= datalength)
	   {
		address -= datalength;
		arrayindex++;
	   }
	  else break;
	 }
	if (arrayindex > wrappers.length) return;

	currentWrapper = arrayindex;
    currentIndex = address;
    currentBuffer = wrappers[currentWrapper].getByteArray();
   }

  //インデックス位置から連続読み込み
  public byte read()
   {
    if (currentWrapper > wrappers.length) throw new IllegalArgumentException(); 

    byte[] array = wrappers[currentWrapper].getByteArray();
    byte result = array[currentIndex];
    currentIndex++;
    
    if (currentIndex >= wrappers[currentWrapper].getDataLength())
     {
      currentIndex = 0;
      currentWrapper++;
     }
    return result;
   }
  
  @Override
  public int read(int address) //読み込みメソッド
   {
    int arrayindex = 0;

    for(int i = 0; i < wrappers.length; i++)
	 {
      int datalength = wrappers[i].getDataLength();
      
	  if (address >= datalength)
	   {
		address -= datalength;
		arrayindex++;
	   }
	  else break;
	 }

    if (arrayindex > wrappers.length) throw new IllegalArgumentException("Out of Bounds");

	return wrappers[arrayindex].read(address);
   } 


  @Override
  public int getDataLength()
   {
	int result = 0;  

	for(ByteArrayWrapper array: wrappers)
	 {
	  result += array.getDataLength();	
	 }
	return result;
   }


  @Override
  public void setDataLength(int a)
   {}


  @Override
  public int getBufferSize()
   {
	int result = 0;  

	for(ByteArrayWrapper array: wrappers)
     {
      result += array.getBufferSize();	
     }
    return result;
   }
 }
