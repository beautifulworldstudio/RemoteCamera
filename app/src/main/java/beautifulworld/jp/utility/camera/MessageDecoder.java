package beautifulworld.jp.utility.camera;

public class MessageDecoder
 {
  private MessageReceiver receiver;	//���b�Z�[�W���󂯎��N���X

  
  public MessageDecoder(MessageReceiver receiver)
   {
	this.receiver = receiver;  
   }	
	
  public boolean decodeMessage(byte[] data)
   {
	return false;  
   }
 }
