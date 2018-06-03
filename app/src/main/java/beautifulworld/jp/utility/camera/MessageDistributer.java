package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class MessageDistributer
 {
  private static ArrayList<MessageReceiver> subscriber = new ArrayList<MessageReceiver>(); 


  //メッセージ送信登録  
  public static void addMessageSubscriber(MessageReceiver registee)
   {
    for(MessageReceiver member: subscriber)
     {
	  if (member == registee) return; //既に登録されている場合は再登録しない。	
     }

    subscriber.add(registee);
   }

  //メッセージ送信解除
  public static void removeMessageSubscriber(MessageReceiver withdrawer)
   {
    for(MessageReceiver member: subscriber)
     {
  	  if (member == withdrawer) //登録されているオブジェクトと一致した。	
       {
  		subscriber.remove(withdrawer);
  		return;
  	   }
  	 }
   }

  //メッセージ配信
  public static void sendMessage(int code , Object extradata)
   {
    for(MessageReceiver member: subscriber)
     {
      //メッセージ送信	
     }
   }
 }
