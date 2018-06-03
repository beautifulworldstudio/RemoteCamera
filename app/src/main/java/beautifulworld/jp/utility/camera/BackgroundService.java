package beautifulworld.jp.utility.camera;

import android.app.Activity;


public interface BackgroundService
 {
  public void setActivity(Activity act);
  public void setConnecter(EOSConnecter connecter);
  public void removeActivity();
  public void disconnect();
  public boolean isBound(); 
 }
