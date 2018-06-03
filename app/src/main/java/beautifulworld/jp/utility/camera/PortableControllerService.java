package beautifulworld.jp.utility.camera;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;


public class PortableControllerService extends Service
 {
  private EOSConnecter connecter;
  private Activity bound;

  
  @Override
  public void onDestroy()
   {
	super.onDestroy();

	if (connecter != null)
	 {
	  connecter.disconnect();
      connecter = null; 
	 }
   }
  
  @Override
  public IBinder onBind(Intent intent)
   {
    Toast.makeText(this, "passed OnBind", Toast.LENGTH_SHORT).show();
    return new ServiceBinder();
   }


  @Override
  public void onRebind(Intent intent)
   {
    Toast.makeText(this, "passed OnReBind", Toast.LENGTH_SHORT).show();

    super.onRebind(intent);
   }

  
  @Override
  public boolean onUnbind(Intent i)
   {
	if (connecter != null) connecter.removeActivity(bound);

	bound = null;
    return true;  
   }
  

  public boolean setActivity(Activity act)
   {
	if (bound != null) return false;  

    bound = act;

    if (connecter != null) connecter.setActivity(act);

   	return true;
   }

  
  public boolean setConnecter(EOSConnecter connecter)
   {
    if (this.connecter != null) return false;

    connecter.setService(this);
    this.connecter = connecter;

    if (bound != null){ connecter.setActivity(bound);}
    Toast.makeText(this, "passed setconnecter", Toast.LENGTH_SHORT).show();
    return true; 
   }

  public void removeActivity()
   {
	if (bound != null)
	 {
	  connecter.removeActivity(bound);
      bound = null;
	 }
   }

  //Activityからの切断の指示を受ける
  public void disconnect()
   {
	connecter.disconnect();  
   }
  
  //EOSConnecterから切断を受け取る
  public void connecterStopped()
   {
	connecter = null;

	//activityに待機モード移行を指示

	//activityの参照を消す
	bound = null;

	stopSelf();
   }

  class ServiceBinder extends Binder
   {
    public PortableControllerService getService()
     {
      return PortableControllerService.this;
     }
   }
 }

