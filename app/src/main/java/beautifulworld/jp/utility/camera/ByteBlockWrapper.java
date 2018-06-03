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
  public void write8(int address, int value){} //�������݃��\�b�h 
  @Override
   public void write16(int address, int value){} //�������݃��\�b�h 
  @Override
  public void write32(int address, int value){} //�������݃��\�b�h 

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

  //�C���f�b�N�X�ʒu����A���ǂݍ���
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
  public int read(int address) //�ǂݍ��݃��\�b�h
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
