package beautifulworld.jp.utility.camera.net;

import beautifulworld.jp.utility.camera.ByteArrayWrapper;
import beautifulworld.jp.utility.camera.Callback;
import beautifulworld.jp.utility.camera.Queue;

public class WifiQueue extends Thread implements Queue
 {
  //����R�[�h�ƃR�[���o�b�N���Z�b�g����
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

  //���C�����[�v
  @Override
  public void run()
   {
	  
   }
 }
