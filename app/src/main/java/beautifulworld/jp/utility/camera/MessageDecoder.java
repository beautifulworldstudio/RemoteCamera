package beautifulworld.jp.utility.camera;

public class MessageDecoder
 {
  private MessageReceiver receiver;	//メッセージを受け取るクラス

  
  public MessageDecoder(MessageReceiver receiver)
   {
	this.receiver = receiver;  
   }	
	
  public boolean decodeMessage(byte[] data)
   {
	return false;  
   }
 }
