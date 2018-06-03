package beautifulworld.jp.utility.camera.net;

import beautifulworld.jp.utility.camera.ByteArrayWrapper;
import beautifulworld.jp.utility.camera.Callback;
import beautifulworld.jp.utility.camera.Queue;

public class WifiQueue extends Thread implements Queue
 {
  //制御コードとコールバックをセットする
  @Override
  public synchronized boolean setControlCode(ByteArrayWrapper[] codes, Callback destination)
   {
	return false;  
   }
  
  @Override
  public void startLoop()
   {
	start();  
   }
  
  @Override
  public void stopLoop(){}

  @Override
  public void close(){}

  //メインループ
  @Override
  public void run()
   {
	  
   }
 }
