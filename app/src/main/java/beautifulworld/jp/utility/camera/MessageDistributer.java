package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class MessageDistributer
 {
  private static ArrayList<MessageReceiver> subscriber = new ArrayList<MessageReceiver>(); 


  //���b�Z�[�W���M�o�^  
  public static void addMessageSubscriber(MessageReceiver registee)
   {
    for(MessageReceiver member: subscriber)
     {
	  if (member == registee) return; //���ɓo�^����Ă���ꍇ�͍ēo�^���Ȃ��B	
     }

    subscriber.add(registee);
   }

  //���b�Z�[�W���M����
  public static void removeMessageSubscriber(MessageReceiver withdrawer)
   {
    for(MessageReceiver member: subscriber)
     {
  	  if (member == withdrawer) //�o�^����Ă���I�u�W�F�N�g�ƈ�v�����B	
       {
  		subscriber.remove(withdrawer);
  		return;
  	   }
  	 }
   }

  //���b�Z�[�W�z�M
  public static void sendMessage(int code , Object extradata)
   {
    for(MessageReceiver member: subscriber)
     {
      //���b�Z�[�W���M	
     }
   }
 }
