package beautifulworld.jp.utility.camera.net;

import beautifulworld.jp.utility.camera.Callback;
import beautifulworld.jp.utility.camera.ReplyContainer;


public class WifiConnecter extends Thread implements Callback
 {
  private WifiConnecter wificonnection;

  private WifiConnecter(){}
  
  //新しい接続を返す
  public static WifiConnecter newInstance() throws IllegalArgumentException
   {
    WifiConnecter result = null;

    return result;
   }
	
	@Override
  public void addData(ReplyContainer reply)
   {
	//データ列を返す  
   }

  @Override
  public void debug(String arg){}

  //メインループ
  @Override
  public void run(){}
 }
