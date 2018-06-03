package beautifulworld.jp.utility.camera.eos6d;

import beautifulworld.jp.utility.camera.Parameters;

public class EOS6DParameters implements Parameters
 {
  private final String[] ShutterSpeedName = new String[] {
		  "30sec", "25sec", "20sec", "15sec", "13sec","10sec","8sec", 
		  "6sec","5sec", "4sec", "3.2sec", "2.5sec", "2.0sec",
		  "1.6sec","1.3sec", "1.0sec", "0.8sec", "0.6sec", "0.5sec",
		  "0.4sec", "0.3sec","4", "5", "6", "8",
		  "10", "13", "15", "20", "25", "30",
		  "40","50", "60", "80", "100", "125",
		  "160","200", "250", "320", "400", "500",
		  "640", "800", "1000", "1250", "1600", "2000",
		  "2500","3200", "4000", "5000", "6400", "8000"};

  private final int[] ShutterSpeedValue = new int[]{
		   0x10, 0x13, 0x15, 0x18, 0x1b, 0x1d, 0x20,
		   0x23, 0x25, 0x28, 0x2b, 0x2d, 0x30,
		   0x33, 0x35, 0x38, 0x3b, 0x3d, 0x40,
		   0x43, 0x45, 0x48, 0x4b, 0x4d, 0x50,
		   0x53, 0x55, 0x58, 0x5b, 0x5d, 0x60,
           0x63, 0x65, 0x68, 0x6b, 0x6d, 0x70,
           0x73, 0x75, 0x78, 0x7b, 0x7d, 0x80,           
           0x83, 0x85, 0x88, 0x8b, 0x8d, 0x90,
           0x93, 0x95, 0x98, 0x9b, 0x9d, 0xa0
          };

  private final String[] ApertureName = new String[]{
		  "1.4", "1.6", "1.8", "2.0", "2.2", "2.5",
		  "2.8", "3.2", "3.5", "4.0", "4.5", "5.0",
		  "5.6", "6.3", "7.1", "8.0", "9.0", "10",
		  "11", "13", "14", "16", "18", "20",
		  "22", "25","29", "32", "36","40","45"};
  
  private final int[] ApertureValue = new int[]{
	      0x10, 0x13, 0x15, 0x18, 0x1b ,0x1d,
	      0x20, 0x23, 0x25, 0x28, 0x2b, 0x2d,
	      0x30, 0x33, 0x35, 0x38, 0x3b, 0x3d,
	      0x40, 0x43, 0x45, 0x48, 0x4b, 0x4d,
	      0x50, 0x53, 0x55, 0x58, 0x5b, 0x5d, 0x60 };
  
  private final String[] ISOName = new String[]{
		  "AUTO", "100", "125", "160", "200" ,"250", 
		  "320", "400", "500", "640", "800", "1000",
		  "1250", "1600", "2000", "2500", "3200", "4000",
		  "5000","6400", "12800"};
  
  private final int[] ISOValue = new int[]{
		  0x00, 0x48, 0x4b, 0x4d, 0x50, 0x53,
		  0x55, 0x58, 0x5b, 0x5d, 0x60, 0x63,
		  0x65, 0x68, 0x6b, 0x6d, 0x70, 0x73,
		  0x75, 0x78, 0x80 };

  private final String[] AFModeName = new String[] {"ONE SHOT", "AI FOCUS","AI SERVO"};
  private final int[] AFModeValue = new int[]{ 0x00, 0x02, 0x01 };
  
  private final String[] WhiteBalanceName = new String[]{"Auto", "SunLight", "Shadow", "Cloudy", "Valb", "white", "Strobe", "Manual", "K"};
  private final int[] WhiteBalanceValue = new int[]{ 0x00, 0x01, 0x08, 0x02, 0x03, 0x04, 0x05, 0x06, 0x09 };

  private final String[] MeteringModeName = new String[]{ "評価測光", "部分測光", "スポット測光", "平均測光" };
  private final int[] MeteringModeValue = new int[]{ 0x03, 0x04, 0x01, 0x05 };
  
  private final String[] DriveModeName = new String[] { "1枚撮影", "高速連射", "低速連射", "10秒タイマー", "2秒タイマー" };
  private final int[] DriveModeValue = new int[]{ 0x00, 0x04, 0x05, 0x10, 0x11 }; 
  
  private final String[] StyleName = new String[] {
		  "Standard", "Portlait", "Landscape", "Neutral", "Faithful", 
		  "Monochrome" , "Use Def 1" ,"User Def 2", "User Def3" };

  private int ShutterSpeed;
  private int ISO;
  private int Aperture;
  private int AFMode;
  private int WhiteBalance;
  private int PictureStyle;
  private int MeteringMode;
  private int DriveMode;
  
  public EOS6DParameters()
   {
   }

  //シャッタースピードセット
  @Override
  public synchronized boolean setShutterSpeed(int ss)
   {
    int index = lookupValue(ShutterSpeedValue, ss);

    if (index != -1) 
     {	
   	  ShutterSpeed = index;  
      return true;
     }
    return false;
   }

  //シャッタースピード取得
  @Override
  public String getCurrentShutterSpeed()
   {
	String result = null;  

	try { result = ShutterSpeedName[ShutterSpeed]; }
	catch(Exception e){ result = "Error"; }  

	return result;
   } 


  @Override
  public synchronized int getHigherShutterSpeed()
   {
	return ShutterSpeedValue[ShutterSpeed + 1];
   }
  
  
  @Override
  public int getLowerShutterSpeed()
   {
	return ShutterSpeedValue[ShutterSpeed - 1];
   }

  //絞りセット
  @Override  
  public synchronized boolean setAperture(int aperture)
   {
    int index = lookupValue(ApertureValue, aperture);

    if (index != -1)
     {
      Aperture = index;
      return true; 
     }

    return false;
   }

  //絞り取得
  @Override
  public String getCurrentAperture()
   {
	String result = null;  

	try { result = ApertureName[Aperture]; }
	catch(Exception e){ result = "Error"; }  

	return result;
   } 

  @Override
  public int getHigherAperture()
   {
	return ApertureValue[Aperture + 1];
   }

  @Override
  public int getLowerAperture()
   {
	return ApertureValue[Aperture - 1];
   }

  //ISO感度セット
  @Override
  public synchronized boolean setISO(int iso)
   {
    int index = lookupValue(ISOValue, iso);

    if (index != -1)
     {
      ISO = index;  
      return true;
     }

    return false;
   }

  //ISO感度取得
  @Override
  public String getCurrentISO()
   {
	String result = null;  

	try { result = ISOName[ISO]; }
	catch(Exception e){ result = "Error"; }  

	return result;
   }
  
  @Override
  public int getHigherISO()
   {
    return ISOValue[ISO + 1];
   }

  @Override
  public int getLowerISO()
   {
	return ISOValue[ISO - 1];
   }

  //ホワイトバランスセット
  @Override
  public synchronized boolean setWhiteBalance(int wb)
   {
    int index = lookupValue(WhiteBalanceValue, wb);

    if (index != -1)
     {
      WhiteBalance = index;
      return true; 
     }

    return false;
   }

  //ホワイトバランス取得
  @Override
  public String getCurrentWhiteBalance()
   {
	String result = null;  

	try { result = WhiteBalanceName[WhiteBalance]; }
	catch(Exception e){ result = "Error"; }  

	return result;
   }
  
  public int getNextWhiteBalance(){return 0;}
  public int getPrevWhiteBalance(){return 0;}
  
  //測光モードセット
  @Override
  public synchronized boolean setMeteringMode(int metering)
   {
    int index = lookupValue(MeteringModeValue, metering);

    if (index != -1)
     {
      MeteringMode = index;
      return true; 
     }

    return false;
   }

  //測光モード取得
  @Override
  public String getCurrentMeteringMode()
   {
	String result = null;  

	try { result = MeteringModeName[MeteringMode]; }
	catch(Exception e){ result = "Error"; }  

	return result;
   } 

  public int getNextMeteringMode()
   {
    int next = MeteringMode + 1;
    if (next >= MeteringModeValue.length) return MeteringModeValue[0];

    return MeteringModeValue[next];
   }

  public int getPrevMeteringMode()
   {
    int next = MeteringMode - 1;
    if (next < 0) return MeteringModeValue[0];

    return MeteringModeValue[next];
   }

  //AFモードセット
  //オートフォーカスモードセット
  @Override
  public synchronized boolean setAFMode(int af)
   {
    int index = lookupValue(AFModeValue, af);

    if (index != -1)
     {
      AFMode = index;
      return true; 
     }

    return false;
   }

  //オートフォーカスモード取得
  //AFモード取得
  @Override
  public String getCurrentAFMode()
   {
	String result = null;  

	try { result = AFModeName[AFMode]; }
	catch(Exception e){ result = "Error"; }  

	return result;
   }

  public int getNextAFMode()
   {
    int next = AFMode + 1;
    if (next >= AFModeValue.length) return AFModeValue[0];

    return AFModeValue[next];
   }

  public int getPrevAFMode()
   {
    int next = AFMode - 1;
    if (next < 0) return AFModeValue[0];

    return AFModeValue[next];
   }

  //連射
  @Override
  public synchronized boolean setDriveMode(int drivemode)
   {
    int index = lookupValue(DriveModeValue, drivemode);

    if (index != -1)
     {
      DriveMode = index;
      return true; 
     }

 	return false;  
   }

  //連射モード取得
  public String getCurrentDriveMode()
   {
	String result = null;  

	try { result = DriveModeName[DriveMode]; }
	catch(Exception e){ result = "Error"; }  

	return result;
   }
  
  public int getNextDriveMode()
   {
    return 0;
   }

  public int getPrevDriveMode()
   {
    return 0;
   }

  //記録画質
  @Override
  public boolean setQualityMode(int quality)
   {
	return false;  
   }

  public String getCurrentQualityMode(){return "";} 
  public int getNextQualityMode(){return 0;}
  public int getPrevQualityMode(){return 0;}
  
  //ピクチャースタイル
  @Override
  public boolean setStyleMode(int style)
   {
	return false;  
   }

  public String getCurrentStyleMode(){return "";} 
  public int getNextStyleMode(){return 0;}
  public int getPrevStyleMode(){return 0;}
  

  private int lookupValue(int[] numbers, int value)
   {
	for(int i = 0; i < numbers.length; i++)
	 {
	  if (numbers[i] == value)
	   {
        return i;
	   }
	 }    
	return -1;  
   }
 }
