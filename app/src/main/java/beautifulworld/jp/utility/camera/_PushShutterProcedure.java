package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class _PushShutterProcedure implements Procedure, Callback
 {
  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;

  public _PushShutterProcedure(MessageReceiver receiver)
   {
	this.receiver = receiver;  

	replydatas = new ArrayList<ReplyContainer>();
   }

  @Override
  public void startProcedure(Queue q)
   {
    boolean loop = true;
    
    ByteArrayWrapper wrapper = ByteArrayManager.getByteWrapper(20);
 	wrapper.write32(0x00, 20);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9128);//コード
    wrapper.write32(0x0c, 1);
    wrapper.write32(0x10, 0);

    q.setControlCode(new ByteArrayWrapper[]{ wrapper }, this);
    
    while(loop)
     {
 	  if (replydatas.size() != 0)
       {
 		ReplyContainer repcon = null;

 		synchronized(this) { repcon = replydatas.remove(0);	}

 		if (repcon.serialnumber == 0x03)
         {
          loop = false;
     	  receiver.debugMes("code 0x9028(1) replied " + repcon.replycode);
         }
        repcon.release();
       }  
     else
      {
   	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
      }
    }//end of while loop

    wrapper = ByteArrayManager.getByteWrapper(20);
    wrapper.write32(0x00, 16);
    wrapper.write16(0x04, 1);
    wrapper.write16(0x06, 0x9128);
    wrapper.write32(0x0c, 0x02);
    wrapper.write32(0x10, 0);
    
    q.setControlCode(new ByteArrayWrapper[]{ wrapper }, this);
    loop = true;

    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}

  		if (repcon.serialnumber == 0x03)
         {
          loop = false;
      	  receiver.debugMes("code 0x9028(2) replied " + repcon.replycode);
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop

    wrapper = ByteArrayManager.getByteWrapper(16);
  	wrapper.write32(0x00, 16);//パケット長さ
    wrapper.write8(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9129);//コード
    wrapper.write32(0x0c, 2);
    
    while (!q.setControlCode(new ByteArrayWrapper[]{wrapper}, this));
    loop = true;

    while(loop)
     {
      if (replydatas.size() != 0)
       {
    	ReplyContainer rep = null;
        synchronized(this) { rep = replydatas.remove(0); }
         
        if (rep.serialnumber == 0x03)
         {
          loop = false;
         }
        rep.release();
       }
      else
       {
    	//タイムアウト設定
       }
     }//end of while loop


    wrapper = ByteArrayManager.getByteWrapper(16);
   	wrapper.write32(0x00, 16);//パケット長さ
    wrapper.write8(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9129);//コード
    wrapper.write32(0x0c, 1);
     
    while (!q.setControlCode(new ByteArrayWrapper[]{wrapper}, this));
    loop = true;

     while(loop)
      {
       if (replydatas.size() != 0)
        {
     	ReplyContainer rep = null;
         synchronized(this) { rep = replydatas.remove(0); }
          
         if (rep.serialnumber == 0x03)
          {
           loop = false;
           receiver.debugMes("code 0x9029(1) replied " + rep.replycode);
          }
         rep.release();
        }
       else
        {
     	//タイムアウト設定
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
