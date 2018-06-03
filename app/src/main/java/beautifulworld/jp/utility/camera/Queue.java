package beautifulworld.jp.utility.camera;

public interface Queue
 {
  public boolean setControlCode(ByteArrayWrapper[] codes, Callback destination);
  public void startLoop();
  public void stopLoop();
  public void close();
 }
