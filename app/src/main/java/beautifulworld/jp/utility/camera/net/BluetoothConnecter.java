package beautifulworld.jp.utility.camera.net;

import beautifulworld.jp.utility.camera.Callback;
import beautifulworld.jp.utility.camera.ReplyContainer;


public class BluetoothConnecter extends Thread implements Callback
 {
  private BluetoothConnecter BTconnection;

  private BluetoothConnecter(){}
  
  //�V�����ڑ���Ԃ�
  public static BluetoothConnecter newInstance() throws IllegalArgumentException
   {
    BluetoothConnecter result = null;

    
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
