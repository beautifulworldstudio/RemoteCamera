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
  
  //BackgroundService�Ƃ�ServiceConnection
  class connection implements ServiceConnection
   {
	@Override  
	public void onServiceConnected(ComponentName name, IBinder binder)
	 {
      //��ɐڑ����Ă���Activity�����邩�H
      try
       {
	    service  = ((PortableControllerService.ServiceBinder)binder).getService();
	    if (!service.setActivity(PortableControllerActivity.this))
	     {
	      //�ҋ@���[�h�ɂ���	 
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
	//lancher�������̓A�C�R������N�������ꍇ�A���ɃA�v�������s���̏ꍇ��
	//USB�ڑ��łȂ��N���̏ꍇ�͉e����^���Ȃ��B
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
 
  //�A�v���J�n
  private void initialize(Intent starter, String servicename)
   {
	if (getBackgroundService(servicename))//����Service�����݂��Ă���
     {
      Toast.makeText(this, "service already exists", Toast.LENGTH_SHORT).show();
      //Service�Ƀo�C���h����
	  try
	   {
		connectionobject = new connection();
	   	if (!bindService(new Intent(this, Class.forName(servicename)), connectionobject, Context.BIND_AUTO_CREATE))
	   	 {
	   	  //�ҋ@���[�h�ɂ���
	   	 }
	   }
	 catch(ClassNotFoundException e){}
     }
    else //Service�Ȃ�
     {
      if (Build.VERSION.SDK_INT < 12)
       {
        //�ҋ@���[�h�ɂ���

         //bluetooth����   
//         setContentView(R.layout.main);	
//         TextView tv = (TextView)findViewById(R.id.msg);
//         tv.setText("level7");
        }
      else if(Build.VERSION.SDK_INT >= 12)
       {
        //EOS�Ɛڑ������݂�
   	    UsbDevice target = starter.getParcelableExtra(UsbManager.EXTRA_DEVICE);

   	    if (target != null)
   	     {
      	  if (connectEOS(target))//�ڑ��ɐ���������Service�N��
           {
   	    	if (!startBackgroundService(servicename))
   	    	 {
   	    	   //USB�ؒf	 
   	    	 }
   		    return;	
           }
   	     }

        //USB���ڑ����ꂽ�̂ł͂Ȃ����A�ڑ������s�����ꍇ
/*   	    if (detectEOS())
         {
       	  if (startBackgroundService(servicename))
	       {
	    	//USB�ؒf	 
	       }
          return;   
         }
          //�ҋ@���[�h�ɂ���
*/
   	   }
     }
   }
  

  //EOS�Ɛڑ�����
  private boolean connectEOS(UsbDevice target)
   {
     //USBDevice�����݂���ꍇ->USB��ڑ����邱�ƂŃA�v�����N���B
	   
	  if (target == null) return false;
	  
	  //�ڑ����ꂽ�J������productID
    int productID = target.getProductId();

    //���\�[�X�擾
    Resources res = getResources();
     
    int[] PIDList = res.getIntArray(R.array.productid);
    String[] connecterClass = res.getStringArray(R.array.connecterclass);

    //�@��̃v���_�N�gID�̔z����̃C���f�b�N�X
    int targetindex = -1;
    for (int i = 0; i < PIDList.length; i++)
     {
      if (PIDList[i] == productID)
       {
    	targetindex = i; break;
       }
     }
    //�z����Ɉ�v����productID��������
    if (targetindex != -1)
     {
      try
       {
        Class targetClass = Class.forName(connecterClass[targetindex]);
        Method initmethod = targetClass.getMethod("newInstance", Activity.class, UsbDevice.class);

        EOSConnecter result = (EOSConnecter)initmethod.invoke(null, this, target); //�C���X�^���X����

        if (result != null)
         {
          connecter = result;
          connecter.connect();//�J�n
          return true;
         }
        }
      catch(Exception e)
       {
       }
     }
    else//��v������̂��Ȃ��ꍇ
     {
      //���ڑ��p��UI���Z�b�g���đҋ@
     }

    return false;
   }

  
  //USB��EOS���ڑ�����Ă��邩��������
  private boolean detectEOS()
   {
	
    //UsbDevice���i�[����Collections���擾
	Collection c = ((UsbManager)getSystemService(USB_SERVICE)).getDeviceList().values();
 
    //���ʊi�[�p
	ArrayList<UsbDevice> result = new ArrayList<UsbDevice>();
    
    //UsbDevice�̃��X�g����������
    if (c.size() != 0)
     {
      //productID�̃��X�g���擾
      Resources res = getResources();
      int[] PIDList = res.getIntArray(R.array.productid);

      //���X�g�������擾
      for (Object obj : c)
       {
       	UsbDevice udev = (UsbDevice)obj;

       	//�x���_�[ID�ƈ�v���Ȃ��ꍇ�̓X�L�b�v
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
      else if (detected > 1) //������EOS�̐ڑ������o����
       {
    	  
       } 
     }

    return false;
   }
  

  //�o�b�N�O���E���h�T�[�r�X���N������
  private boolean startBackgroundService(String servicename)
   {
	  Toast.makeText(this, "passed startservice", Toast.LENGTH_SHORT).show();
	try
	 {
	  Class serviceclass = Class.forName(servicename);
      ComponentName name = startService(new Intent(this, serviceclass));

      //service�N�������B�o�C���h����
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
 
  
  //�o�b�N�O���E���h�T�[�r�X��T��
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
    //�T�[�r�X�𐶐�����
    return false;
   }
 }