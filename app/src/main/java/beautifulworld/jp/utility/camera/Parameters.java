package beautifulworld.jp.utility.camera;

public interface Parameters
 {
  public boolean setShutterSpeed(int ss);
  public String getCurrentShutterSpeed(); 
  public int getHigherShutterSpeed();
  public int getLowerShutterSpeed();
  
  public boolean setAperture(int iso);
  public String getCurrentAperture(); 
  public int getHigherAperture();
  public int getLowerAperture();

  public boolean setISO(int iso);
  public String getCurrentISO(); 
  public int getHigherISO();
  public int getLowerISO();

  public boolean setWhiteBalance(int iso);
  public String getCurrentWhiteBalance(); 
  public int getNextWhiteBalance();
  public int getPrevWhiteBalance();
  
  //����
  public boolean setMeteringMode(int iso);
  public String getCurrentMeteringMode(); 
  public int getNextMeteringMode();
  public int getPrevMeteringMode();

  public boolean setAFMode(int iso);
  public String getCurrentAFMode(); 
  public int getNextAFMode();
  public int getPrevAFMode();

  //�A��
  public boolean setDriveMode(int iso);
  public String getCurrentDriveMode(); 
  public int getNextDriveMode();
  public int getPrevDriveMode();

  //�L�^�掿
  public boolean setQualityMode(int iso);
  public String getCurrentQualityMode(); 
  public int getNextQualityMode();
  public int getPrevQualityMode();
  
  //�s�N�`���[�X�^�C��
  public boolean setStyleMode(int iso);
  public String getCurrentStyleMode(); 
  public int getNextStyleMode();
  public int getPrevStyleMode();
 }
