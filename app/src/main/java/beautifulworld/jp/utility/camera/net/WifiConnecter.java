package beautifulworld.jp.utility.camera.net;

import beautifulworld.jp.utility.camera.Callback;
import beautifulworld.jp.utility.camera.ReplyContainer;


public class WifiConnecter extends Thread implements Callback
 {
  private WifiConnecter wificonnection;

  private WifiConnecter(){}
  
  //�V�����ڑ���Ԃ�
  public static WifiConnecter newInstance() throws IllegalArgumentException
   {
    WifiConnecter result = null;

    return result;
   }
	
	@Override
  public void addData(ReplyContainer reply)
   {
	//�f�[�^���Ԃ�  
   }

  @Override
  public void debug(String arg){}

  //���C�����[�v
  @Override
  public void run(){}
 }
