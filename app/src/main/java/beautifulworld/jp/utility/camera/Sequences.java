package beautifulworld.jp.utility.camera;

import android.content.Intent;
import android.content.Context;
import android.app.ActivityManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import java.util.List;


public class Sequences
 {
  //シャッタースピード変更
  public static void changeShutterSpeed(Parameters param, ProcedureHandler handler, boolean higher)
   {
    int code = 0;

    try
     {
      if (higher) code = param.getHigherShutterSpeed();
      else code = param.getLowerShutterSpeed();
     }
    catch(IndexOutOfBoundsException e) { return; }
    
    sendCommand(handler, code, 0xd102);
   }
 
 
  public static void changeAperture(Parameters param, ProcedureHandler handler, boolean higher)
   {
    int code = 0;

    try
     {
      if (higher) code = param.getHigherAperture();
      else code = param.getLowerAperture();
     }
    catch(IndexOutOfBoundsException e) { return; }    

    sendCommand(handler, code, 0xd101);  
   }
  
  public static void  changeISO(Parameters param, ProcedureHandler handler, boolean higher)
   {
    int code = 0;

    try
     {
      if (higher) code = param.getHigherISO();
      else code = param.getLowerISO();
     }
    catch(IndexOutOfBoundsException e) { return; }    

    sendCommand(handler, code, 0xd103); 
   }

  public static void changeDriveMode(Parameters param, ProcedureHandler handler, boolean higher)
   {
    int code = 0;

    try
     {
      if (higher) code = param.getNextDriveMode();
      else code = param.getPrevDriveMode();
     }
    catch(IndexOutOfBoundsException e) { return; }    

    sendCommand(handler, code, 0xd106);  
   }

  public static void changeMeteringMode(Parameters param, ProcedureHandler handler, boolean higher)
   {
    int code = 0;

    try
     {
      if (higher) code = param.getNextMeteringMode();
      else code = param.getPrevMeteringMode();
     }
    catch(IndexOutOfBoundsException e) { return; }    

    sendCommand(handler, code, 0xd107);  
   }
 
  public static void changeAFMode(Parameters param, ProcedureHandler handler, boolean higher)
   {
    int code = 0;

    try
     {
      if (higher) code = param.getNextAFMode();
      else code = param.getPrevAFMode();
     }
    catch(IndexOutOfBoundsException e) { return; }    

    sendCommand(handler, code, 0xd108);  
   }

  public static void changeWhiteBalance(Parameters param, ProcedureHandler handler, boolean higher)
   {
    int code = 0;

    try
     {
      if (higher) code = param.getNextWhiteBalance();
      else code = param.getPrevWhiteBalance();
     }
    catch(IndexOutOfBoundsException e) { return; }    

    sendCommand(handler, code, 0xd109);  
   }

  public static void changeQualityMode(Parameters param, ProcedureHandler handler, boolean higher){}
  public static void changeStyeMode(Parameters param, ProcedureHandler handler, boolean higher){}

  private static void sendCommand(ProcedureHandler handler, int code, int function)
   {
    //Procedureを生成
    _ChangePropertyProcedure proc = new _ChangePropertyProcedure();
    ByteArrayWrapper[] controlcode = new ByteArrayWrapper[2];

    controlcode[0] = ByteArrayManager.getByteWrapper(12);
    controlcode[1] = ByteArrayManager.getByteWrapper(24);

    controlcode[0].write32(0x00, 12);
    controlcode[0].write16(0x04, 1);
    controlcode[0].write16(0x06, 0x9110);

    controlcode[1].write32(0x00, 24);
    controlcode[1].write16(0x04, 2);
    controlcode[1].write16(0x06, 0x9110);
    controlcode[1].write32(0x0c, 12);
    controlcode[1].write32(0x10, function);//0xd102
    controlcode[1].write32(0x14, code);

    //Procedureにデータを印加
    proc.setCode(controlcode);
   
    //Procedureセット
    handler.addProcedure(proc);
   }
 }
