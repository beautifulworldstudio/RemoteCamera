package beautifulworld.jp.utility.camera;

import android.app.Activity;
import android.hardware.usb.UsbDevice;

import android.mtp.MtpDevice;
import android.widget.TextView;

public class BasicConnecter  implements MessageReceiver, EOSConnecter
 {
  private UsbDevice usbdevice;
  private MtpDevice ptpdevice;
  private EOSParameters params;
  private PortableControllerService service;

  private BasicConnecter(){}

  public static BasicConnecter newInstance(Activity act, UsbDevice target)
   {
	BasicConnecter result = new BasicConnecter();  

	result.usbdevice = target;
	result.ptpdevice = new MtpDevice(target);

	if (result.ptpdevice == null) return null;

    result.params = new EOSParameters();

    return result;
   }
  
   
  //メッセージ受け取り
  @Override
  public void debugMes(String arg)
   {
   }  

  //serviceセット
  @Override
  public void setService(PortableControllerService ser)
   {
	service = ser;
   }
 
  @Override
  public void changeAperture(int code){}
  @Override
  public void changeDriveMode(int code){} //0xD106
  @Override
  public void changeMeteringMode(int code){}
  @Override
  public void changeAFMode(int code){}
  @Override
  public void changeWhiteBalance(int code){} //0xD109
  @Override
  public void changeShutterSpeed(int code){}
  @Override
  public void changeISO(int code){}
  @Override
  public void connectionInitialized(){}
  @Override
  public void connectionFinalized(){}
  @Override
  public void checkStatusFinished(){}  

  @Override
  public void connect(){}
  public void removeActivity(Activity act){}
  public void setActivity(Activity act){}

  //切断
  @Override
  public void disconnect(){}
 }
