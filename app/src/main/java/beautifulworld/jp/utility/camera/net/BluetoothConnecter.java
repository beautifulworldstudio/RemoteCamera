package beautifulworld.jp.utility.camera.net;

import beautifulworld.jp.utility.camera.Callback;
import beautifulworld.jp.utility.camera.ReplyContainer;


public class BluetoothConnecter extends Thread implements Callback
 {
  private BluetoothConnecter BTconnection;

  private BluetoothConnecter(){}
  
  //新しい接続を返す
  public static BluetoothConnecter newInstance() throws IllegalArgumentException
   {
    BluetoothConnecter result = null;

    
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
