package beautifulworld.jp.utility.camera.eos7d;

import beautifulworld.jp.utility.camera.*;

import java.util.ArrayList;

public class _StartLiveViewProcedure implements Procedure, Callback
 {
  public static final int code = 0x02; //ライブビュー開始時のコード

  private Queue usbloop;
  private MessageReceiver receiver;
  private MessageDecoder7D decoder;
  private LiveViewMessageReceiver LVreceiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;
  
  public _StartLiveViewProcedure(MessageReceiver receiver, LiveViewMessageReceiver LV, MessageDecoder7D dec)
   {
    LVreceiver = LV;
	this.receiver = receiver;
	decoder = dec;

    replydatas = new ArrayList<ReplyContainer>();
   }

  
  @Override
  public void startProcedure(Queue q)
   {
    boolean loop = true;
    
    ByteArrayWrapper wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9110);//コード

    ByteArrayWrapper changedata = ByteArrayManager.getByteWrapper(24);
    changedata.write32(0x00, 24);
    changedata.write16(0x04, 2);
    changedata.write16(0x06, 0x9110);
    changedata.write32(0x0c, 12);
    changedata.write32(0x10, 0xd1b0);
    changedata.write32(0x14, code);

    q.setControlCode(new ByteArrayWrapper[]{wrapper, changedata}, this);

    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 3)
         {
      	  loop = false;
          if (repcon.replycode != 0x2001)
          {
       	  return;
          }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop
    wrapper =null;
    changedata=null;

    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9110);//コード

    changedata = ByteArrayManager.getByteWrapper(24);
    changedata.write32(0x00, 24);
    changedata.write16(0x04, 2);
    changedata.write16(0x06, 0x9110);
    changedata.write32(0x0c, 12);
    changedata.write32(0x10, 0xd1b3);
    changedata.write32(0x14, 0);

    q.setControlCode(new ByteArrayWrapper[]{wrapper, changedata}, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 3)
         {
      	  loop = false;
          if (repcon.replycode == 0x2001)
           {
       	    LVreceiver.LiveViewStarted();
           }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop 
    wrapper =null;
    changedata=null;

    //動画準備状態にする

    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9133);//コード

    q.setControlCode(new ByteArrayWrapper[]{wrapper }, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
 	    if (repcon.serialnumber == 3)
         {
      	  loop = false;
          if (repcon.replycode == 0x2001)
           {
        	LVreceiver.MovieModeStarted();
           }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop 

    /*
    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9110);//コード

    changedata = ByteArrayManager.getByteWrapper(24);
    changedata.write32(0x00, 24);
    changedata.write16(0x04, 2);
    changedata.write16(0x06, 0x9110);
    changedata.write32(0x0c, 12);
    changedata.write32(0x10, 0xd11c);
    changedata.write32(0x14, 0x05);

    q.setControlCode(new ByteArrayWrapper[]{wrapper, changedata}, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 3)
         {
      	  loop = false;
//          receiver.debugMes("liveview replied " + repcon.replycode);
          if (repcon.replycode == 0x2001)
           {
        	  if (code == start) LVreceiver.LiveViewStarted();
           }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop
/*
    wrapper =null;
    changedata=null;
    
    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9116);//コード

     q.setControlCode(new ByteArrayWrapper[]{wrapper }, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
  	    if (repcon.serialnumber == 0x02 ) 
    	 {
          if (repcon.replydatas.length == 1) decoder.decodeMessage(repcon.replydatas[0]); //デコードする
          else decoder.decodeMessage(new ByteBlockWrapper(repcon.replydatas)); 
    	 }
  	    else if (repcon.serialnumber == 3)
         {
      	  loop = false;
 //         receiver.debugMes("liveview replied " + repcon.replycode);
          if (repcon.replycode != 0x2001)
           {
        	  return;
           }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop 
    wrapper =null;
    changedata=null;
    
    wrapper = ByteArrayManager.getByteWrapper(12);
   	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9101);//コード

      q.setControlCode(new ByteArrayWrapper[]{wrapper }, this);
      loop = true;
      while(loop)
       {
        if (replydatas.size() != 0)
         {
    	  ReplyContainer repcon = null;

    	  synchronized(this) { repcon = replydatas.remove(0);	}
     	  if (repcon.serialnumber == 3)
           {
        	  loop = false;
   //         receiver.debugMes("liveview replied " + repcon.replycode);
            if (repcon.replycode != 0x2001)
             {
          	  return;
             }
           }
          repcon.release();
         }  
        else
         {
      	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
         }
       }//end of while loop 
      wrapper =null;
      changedata=null;
 
    changedata = ByteArrayManager.getByteWrapper(24);
    changedata.write32(0x00, 24);
    changedata.write16(0x04, 1);
    changedata.write16(0x06, 0x911a);
    changedata.write32(0x0c, 0x032d59e9);
    changedata.write32(0x10, 0x1000);
    changedata.write32(0x14, 0x01);

    q.setControlCode(new ByteArrayWrapper[]{changedata}, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 3)
         {
      	  loop = false;
 //         receiver.debugMes("liveview replied " + repcon.replycode);
          if (repcon.replycode != 0x2001)
           {
            return;
           }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop 
    wrapper =null;
    changedata=null;

    wrapper = ByteArrayManager.getByteWrapper(16);
 	wrapper.write32(0x00, 16);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9102);//コード
    wrapper.write32(0x0c, 0x010001);
    
    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 2){ 
        	//receiver.debugMes("9102 replied " + repcon.replycode + ":" + repcon.packetlength);
        	}
  		else if (repcon.serialnumber == 3)
         {
      	  loop = false;
          
          if (repcon.replycode != 0x2001)
           {
        	return;
           }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop
    wrapper =null;
    changedata=null;

    wrapper = ByteArrayManager.getByteWrapper(16);
 	wrapper.write32(0x00, 16);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9102);//コード
    wrapper.write32(0x0c, 0x030000);
    
    q.setControlCode(new ByteArrayWrapper[]{wrapper}, this);
    loop = true;
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.serialnumber == 3)
         {
      	  loop = false;
          //receiver.debugMes("liveview replied " + repcon.replycode);
          if (repcon.replycode == 0x2001)
           {
        	if (code == start) LVreceiver.LiveViewStarted();
           }
         }
        repcon.release();
       }  
      else
       {
    	//ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
       }
     }//end of while loop
     */
    }

  @Override
  public void debug(String arg){}

  @Override
  public void addData(ReplyContainer reply)
   {
    replydatas.add(reply);
   }
 }
