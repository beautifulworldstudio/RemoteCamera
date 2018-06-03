package beautifulworld.jp.utility.camera;

public interface MessageReceiver
 {
  public void debugMes(String arg);
  public void connectionInitialized();
  public void connectionFinalized();
  public void checkStatusFinished();
  
  public void changeShutterSpeed(int code); //0xD101
  public void changeISO(int code);
  public void changeAperture(int code);

  public void changeDriveMode(int code); //0xD106
  public void changeMeteringMode(int code);
  public void changeAFMode(int code);
  public void changeWhiteBalance(int code); //0xD109

//  public void receiveMessage(int code, Object extradata);
 }
