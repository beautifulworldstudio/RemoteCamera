package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class _FinalizeProcedure implements Procedure, Callback
 {
  private Queue usbloop;
  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;
  

  public _FinalizeProcedure(MessageReceiver receiver)
   {
	this.receiver = receiver;  
	replydatas = new ArrayList<ReplyContainer>();
   }

  
  @Override
  public void startProcedure(Queue q)
   {
	usbloop = q;  
    boolean loop = true;

    ByteArrayWrapper wrapper = ByteArrayManager.getByteWrapper(12);
    
 	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write8(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x911b);//コード
  
    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);

    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 0x03)
         {
          if( repcon.replycode == 0x2001)
           {
      	    loop = false;
      	    receiver.debugMes("code 0x901b successed");
           }
          else receiver.debugMes("code 0x901b failed " + repcon.replycode);
          repcon.release();
         }
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop

   
    wrapper = ByteArrayManager.getByteWrapper(12);
    
 	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write8(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x911c);//コード

    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);
     loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 0x03)
         { 
          if ( repcon.replycode == 0x2001)
           {
      	    loop = false;
      	    receiver.debugMes("code 0x901c successed");
           }
          else receiver.debugMes("code 0x901c failed " + repcon.replycode);
          repcon.release();
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
    wrapper.write32(0x0c, 0);//値

    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 0x03)
         {
          if(repcon.replycode == 0x2001)
           {
      	   loop = false;
      	   receiver.debugMes("code 0x9015 (finalize) successed");
           }
          repcon.release();
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
    wrapper.write16(0x06, 0x9114);//コード
    wrapper.write32(0x0c, 0);//値

    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 0x03)
         {
          if(repcon.replycode == 0x2001)
           {
      	    loop = false;
       	    receiver.debugMes("code 0x9014 (finalize) successed");
            receiver.connectionFinalized();
           }
          repcon.release();
         }
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop
 
   }

  
  @Override
  public void debug(String arg){}
  
  
  @Override
  public void addData(ReplyContainer reply)
   {
    replydatas.add(reply);
   }
 }
