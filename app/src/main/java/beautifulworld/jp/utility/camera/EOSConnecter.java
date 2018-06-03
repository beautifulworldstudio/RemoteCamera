package beautifulworld.jp.utility.camera;

import android.app.Activity;

public interface EOSConnecter
 {
  public void setActivity(Activity act);
  public void setService(PortableControllerService ser); 
  public void removeActivity(Activity act); 
  public void connect();
  public void disconnect();
 } 
