package beautifulworld.jp.utility.camera;

public class ByteArrayWrapper implements ByteArrayContainer
 {
  private byte[] buffer;
  private boolean using;//�g�p�����������t���O
  private int datalength; 
 
  private ByteArrayWrapper(){}

 
  class accessor
   {
    private ByteArrayWrapper outer;

    public accessor(ByteArrayWrapper wrapper)
     {
      outer = wrapper;
     }

    
    public void setFlag(boolean bool)
     {
      using = bool;
     }
 
 
    public boolean getFlag()
	 {
	  return using;	
	 }


    //�A�E�^�[�N���X�̃C���X�^���X��Ԃ�
    public ByteArrayWrapper getOuterInstance()
     {
      return outer;	
     }
   }

  //�A�N�Z�b�T�[�̃C���i�[�N���X�𐶐�
  private accessor getInner(ByteArrayWrapper outer)
    {
	 return new accessor(outer); 
    }

  //�e�ʂ��w�肵�Đ���
  public static ByteArrayWrapper.accessor newInstance(int length)
   {
    if (length <= 0) return null;

    ByteArrayWrapper instance = new ByteArrayWrapper();
	ByteArrayWrapper.accessor  result = instance.getInner(instance);
	instance.datalength = 0;

    //�o�C�g�z��𐶐�����
    instance.buffer = new byte[length];

    return result;  
   }

  @Override
  public void setDataLength(int a)
   {
	if ( a < 0 || a > buffer.length) return;

    datalength = a;
   }
 
  @Override
  public int getDataLength()
   {
	return datalength;  
   }

  @Override
  public int getBufferSize()
   {
	return buffer.length;  
   }

  //�ǂݍ��݃��\�b�h
  @Override
  public int read(int address)
   {
    if (address < 0 || address > datalength) throw new IllegalArgumentException();

    return (int)buffer[address] & 0xff;
   }
  

  @Override 
  public void write8(int address, int value)
   {
	if (address < 0 || address > datalength - 1) return;
	
    buffer[address] = (byte)value;
   }

  @Override
  public void write16(int address, int value)
   {
	if (address < 0 || address > (datalength - 2)) return;

	buffer[address++] = (byte)value;
    buffer[address] = (byte)(value >> 8);
   }

  @Override
  public void write32(int address, int value)
   {
	if (address < 0 || address > (datalength - 4)) return;

	buffer[address++] = (byte)value;
	buffer[address++] = (byte)(value >>> 8);
	buffer[address++] = (byte)(value >>> 16);
	buffer[address] = (byte)(value >>> 24);
   }

  //�o�C�g���Ԃ�
  public byte[] getByteArray()
   {
	return buffer;  
   }
 }
