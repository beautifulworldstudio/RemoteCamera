package beautifulworld.jp.utility.camera;

public class EOSParameters
 {
  private int ShutterSpeed;
  private int Exposure;
  private int ISO;

  //�V���b�^�[�X�s�[�h
  public int getShutterSpeed()
   {
	return ShutterSpeed;
   }

  public void setShutterSpeed(int ss)
   {
	ShutterSpeed = ss;
   }

  //�i��
  public int getExposure()
   {
	return Exposure;
   }

  public void setExposure(int ex)
   {
	Exposure = ex;
   }

  //ISO���x  
  public int getISO()
   {
	return ISO;  
   } 
  
  public void setISO(int iso)
   {
	ISO = iso;  
   }
 }
