package beautifulworld.jp.utility.camera;

public class EOSParameters
 {
  private int ShutterSpeed;
  private int Exposure;
  private int ISO;

  //シャッタースピード
  public int getShutterSpeed()
   {
	return ShutterSpeed;
   }

  public void setShutterSpeed(int ss)
   {
	ShutterSpeed = ss;
   }

  //絞り
  public int getExposure()
   {
	return Exposure;
   }

  public void setExposure(int ex)
   {
	Exposure = ex;
   }

  //ISO感度  
  public int getISO()
   {
	return ISO;  
   } 
  
  public void setISO(int iso)
   {
	ISO = iso;  
   }
 }
