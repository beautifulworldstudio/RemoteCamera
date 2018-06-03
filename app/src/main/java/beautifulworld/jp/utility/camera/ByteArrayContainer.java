package beautifulworld.jp.utility.camera;

public interface ByteArrayContainer
 {
  public void write8(int address, int value); //書き込みメソッド 
  public void write16(int address, int value); //書き込みメソッド 
  public void write32(int address, int value); //書き込みメソッド 
  public int read(int address);  //読み込みメソッド
  public int getDataLength(); //
  public void setDataLength(int a);
  public int getBufferSize();
 }
