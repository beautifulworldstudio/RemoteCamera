package beautifulworld.jp.utility.camera;

public abstract class PropertyAccessor
 {
  private static PropertyAccessor accessor;
	
  public abstract int getAperture();
  public abstract void setAperture(int ss);	

  public abstract int getShutterSpeed();
  public abstract void setShutterSpeed(int ss);	
	
  public abstract int getISO();
  public abstract void setISO(int iso);

  public abstract int getDriveMode();
  public abstract void setDriveMode(int code);

  public abstract int getMeteringMode();
  public abstract void setMeteringMode(int code);
  
  public abstract void getAFMode();
  public abstract void setAFMode(int code);
  
  public abstract void getWhiteBalance();
  public abstract void setWhiteBalance(int code);

  
  //PropertyAccsssor�̋�ۃN���X���Z�b�g����
  protected static void setPropertyAccessor(PropertyAccessor concrete)
   {
	accessor = concrete;   
   }  
  
  //PropertyAccsssor�̋�ۃN���X��Ԃ�
  public static PropertyAccessor getPropertyAccessor()
   {
	return  accessor;  
   } 
 }
