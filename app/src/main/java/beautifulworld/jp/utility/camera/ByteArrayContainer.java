package beautifulworld.jp.utility.camera;

public interface ByteArrayContainer
 {
  public void write8(int address, int value); //�������݃��\�b�h 
  public void write16(int address, int value); //�������݃��\�b�h 
  public void write32(int address, int value); //�������݃��\�b�h 
  public int read(int address);  //�ǂݍ��݃��\�b�h
  public int getDataLength(); //
  public void setDataLength(int a);
  public int getBufferSize();
 }
