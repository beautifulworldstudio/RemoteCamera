package beautifulworld.jp.utility.camera;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class PortableControllerActivity extends Activity
 {
  public static final int VENDERID = 0x04a9;
  private static final String servicepath = "beautifulworld.jp.utility.camera.PortableControllerService";

  private EOSConnecter connecter;
  private PortableControllerService service;
  private connection connectionobject;
  
  //BackgroundServiceとのServiceConnection
  class connection implements ServiceConnection
   {
	@Override  
	public void onServiceConnected(ComponentName name, IBinder binder)
	 {
      //先に接続しているActivityがあるか？
      try
       {
	    service  = ((PortableControllerService.ServiceBinder)binder).getService();
	    if (!service.setActivity(PortableControllerActivity.this))
	     {
	      //待機モードにする	 
	     }
	    else
	     {
	      Toast.makeText(PortableControllerActivity.this, "OnServiceConnecte", Toast.LENGTH_SHORT).show();
	      service.setConnecter(connecter);	
	     }
       }
      catch(Exception e){}
	 }

	@Override
	public void onServiceDisconnected(ComponentName name)
	 {
		
	 }
   }

  @Override
  public void onStart()
   {
	super.onStart();
	if (service != null)
	 {
	  service.setActivity(this);	
	 }
   }

  
  @Override
  public void onCreate(Bundle savedInstanceState)
   {
    super.onCreate(savedInstanceState);
	
    initialize(getIntent(), servicepath);
   }
  
  
  @Override
  public void onNewIntent(Intent i)
   {
    initialize(getIntent(), servicepath);
	//lancherもしくはアイコンから起動した場合、既にアプリが実行中の場合は
	//USB接続でない起動の場合は影響を与えない。
   }

  
  @Override
  public void onPause()
   {
	super.onPause();  
   } 
  

  @Override
  public void onStop()
   {
    super.onStop();

    if (service != null) service.removeActivity();
    if (connectionobject != null) unbindService(connectionobject);
   }

  @Override
  public void onDestroy()
   {
	super.onDestroy();

   }
 
  //アプリ開始
  private void initialize(Intent starter, String servicename)
   {
	if (getBackgroundService(servicename))//既にServiceが存在している
     {
      Toast.makeText(this, "service already exists", Toast.LENGTH_SHORT).show();
      //Serviceにバインドする
	  try
	   {
		connectionobject = new connection();
	   	if (!bindService(new Intent(this, Class.forName(servicename)), connectionobject, Context.BIND_AUTO_CREATE))
	   	 {
	   	  //待機モードにする
	   	 }
	   }
	 catch(ClassNotFoundException e){}
     }
    else //Serviceなし
     {
      if (Build.VERSION.SDK_INT < 12)
       {
        //待機モードにする

         //bluetooth許可   
//         setContentView(R.layout.main);	
//         TextView tv = (TextView)findViewById(R.id.msg);
//         tv.setText("level7");
        }
      else if(Build.VERSION.SDK_INT >= 12)
       {
        //EOSと接続を試みる
   	    UsbDevice target = starter.getParcelableExtra(UsbManager.EXTRA_DEVICE);

   	    if (target != null)
   	     {
      	  if (connectEOS(target))//接続に成功したらService起動
           {
   	    	if (!startBackgroundService(servicename))
   	    	 {
   	    	   //USB切断	 
   	    	 }
   		    return;	
           }
   	     }

        //USBが接続されたのではないか、接続が失敗した場合
/*   	    if (detectEOS())
         {
       	  if (startBackgroundService(servicename))
	       {
	    	//USB切断	 
	       }
          return;   
         }
          //待機モードにする
*/
   	   }
     }
   }
  

  //EOSと接続する
  private boolean connectEOS(UsbDevice target)
   {
     //USBDeviceが存在する場合->USBを接続することでアプリが起動。
	   
	  if (target == null) return false;
	  
	  //接続されたカメラのproductID
    int productID = target.getProductId();

    //リソース取得
    Resources res = getResources();
     
    int[] PIDList = res.getIntArray(R.array.productid);
    String[] connecterClass = res.getStringArray(R.array.connecterclass);

    //機種のプロダクトIDの配列内のインデックス
    int targetindex = -1;
    for (int i = 0; i < PIDList.length; i++)
     {
      if (PIDList[i] == productID)
       {
    	targetindex = i; break;
       }
     }
    //配列内に一致するproductIDがあった
    if (targetindex != -1)
     {
      try
       {
        Class targetClass = Class.forName(connecterClass[targetindex]);
        Method initmethod = targetClass.getMethod("newInstance", Activity.class, UsbDevice.class);

        EOSConnecter result = (EOSConnecter)initmethod.invoke(null, this, target); //インスタンス生成

        if (result != null)
         {
          connecter = result;
          connecter.connect();//開始
          return true;
         }
        }
      catch(Exception e)
       {
       }
     }
    else//一致するものがない場合
     {
      //無接続用のUIをセットして待機
     }

    return false;
   }

  
  //USBにEOSが接続されているか検索する
  private boolean detectEOS()
   {
	
    //UsbDeviceを格納するCollectionsを取得
	Collection c = ((UsbManager)getSystemService(USB_SERVICE)).getDeviceList().values();
 
    //結果格納用
	ArrayList<UsbDevice> result = new ArrayList<UsbDevice>();
    
    //UsbDeviceのリストを検索する
    if (c.size() != 0)
     {
      //productIDのリストを取得
      Resources res = getResources();
      int[] PIDList = res.getIntArray(R.array.productid);

      //リストを順次取得
      for (Object obj : c)
       {
       	UsbDevice udev = (UsbDevice)obj;

       	//ベンダーIDと一致しない場合はスキップ
       	if (udev.getVendorId() != VENDERID) continue;

       	for (int productid: PIDList)
       	 {
       	  if (udev.getProductId() == productid)
       	   {
            result.add(udev);
      		break;   
       	   }
       	 }
       }//end of outer for-loop
      int detected = result.size();

      if (detected == 1)
       {
    	return connectEOS(result.get(0));  
       }
      else if (detected > 1) //複数のEOSの接続を検出した
       {
    	  
       } 
     }

    return false;
   }
  

  //バックグラウンドサービスを起動する
  private boolean startBackgroundService(String servicename)
   {
	  Toast.makeText(this, "passed startservice", Toast.LENGTH_SHORT).show();
	try
	 {
	  Class serviceclass = Class.forName(servicename);
      ComponentName name = startService(new Intent(this, serviceclass));

      //service起動成功。バインドする
      if (name != null)
       {
    	  Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
    	Intent i = new Intent(this, serviceclass); 
    	connectionobject = new connection(); 
    	return bindService(i, connectionobject, Context.BIND_AUTO_CREATE);
       }
      else{ Toast.makeText(this, "service starting fsileds", Toast.LENGTH_LONG).show();}
	 }
	catch(ClassNotFoundException e){Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();}

	return false;
   }
 
  
  //バックグラウンドサービスを探す
  private boolean getBackgroundService(String servicename)
   {
	ActivityManager actman = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
	List<ActivityManager.RunningServiceInfo> l = actman.getRunningServices(Integer.MAX_VALUE);

	for (ActivityManager.RunningServiceInfo info : l)
	 {
      if (servicename.equals(info.service.getClassName()))
       {
    	return true;
       }
     }
    //サービスを生成する
    return false;
   }
 }