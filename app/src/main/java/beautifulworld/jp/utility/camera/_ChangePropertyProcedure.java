package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class _ChangePropertyProcedure implements Procedure, Callback
 {
  private ByteArrayWrapper[] controlcode;
//  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;


  public _ChangePropertyProcedure()
   {
//	this.receiver = receiver;  

	replydatas = new ArrayList<ReplyContainer>();
   }

  @Override	
  public void startProcedure(Queue q)
   {
	if( controlcode == null) return;

	boolean loop = true;

	q.setControlCode(controlcode, this);

    while(loop)
     {
 	  if (replydatas.size() != 0)
       {
 		ReplyContainer repcon = null;

 		synchronized(this) { repcon = replydatas.remove(0);	}
      
     	loop = false;
        repcon.release();
//     	receiver.debugMes("code 0x9010 replied " + repcon.replycode);
       }  
      else
       {
   	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop}
   }

  
  @Override
  public void addData(ReplyContainer reply)
   {
	replydatas.add(reply);
   }

  @Override
  public void debug(String arg){}

  //����R�[�h����
  public void  setCode(ByteArrayWrapper[] datas)
   {
	controlcode = datas;  
   }
 }
