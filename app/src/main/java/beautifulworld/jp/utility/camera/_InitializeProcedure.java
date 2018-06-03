package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class _InitializeProcedure implements Procedure, Callback
 {
  private Queue usbloop;
  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;

  
  public _InitializeProcedure(MessageReceiver receiver)
   {
	this.receiver = receiver;  
	replydatas = new ArrayList<ReplyContainer>();
   }

  
  @Override
  public void startProcedure(Queue q)
   {
	usbloop = q;
    boolean loop = true;
 
    ByteArrayWrapper wrapper = ByteArrayManager.getByteWrapper(16);
    
 	wrapper.write32(0x00, 16);//パケット長さ
    wrapper.write8(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9114);//コード
    wrapper.write32(0x0c, 1);//値

    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);

    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 0x03 && repcon.replycode == 0x2001)
         {
      	  loop = false;
          repcon.release();
      	  //receiver.debugMes("code 0x9014 successed");
         }
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop

    wrapper = ByteArrayManager.getByteWrapper(16);
 	wrapper.write32(0x00, 16);//パケット長さ
    wrapper.write8(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9115);//コード
    wrapper.write32(0x0c, 1);//値
   
    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);
    loop = true;

    while(loop)
     {
   	  if (replydatas.size() != 0)
   	   {
   	    ReplyContainer repcon = null;

   	    synchronized(this) { repcon = replydatas.remove(0);	}	
       	if (repcon.serialnumber == 0x03 && repcon.replycode == 0x2001)
         {
          loop = false;
          
       	  receiver.connectionInitialized();
         }
       	else
       	 {
          receiver.debugMes("code 0x9015 error " + repcon.serialnumber + ":" + repcon.replycode);
       	 }
        repcon.release();  
       }
      else
   	   {
   	    //receiver.debugMes("passed 0");//タイムアウト設定
   	   }
     }//end of while loop
   }
 
  
  @Override
  public void debug(String arg)
   {
    receiver.debugMes(arg);
   }
  
  @Override
  public synchronized void addData(ReplyContainer reply)
   {
    replydatas.add(reply);
   }
 }
