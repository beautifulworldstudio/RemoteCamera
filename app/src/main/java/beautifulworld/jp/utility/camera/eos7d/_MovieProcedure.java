package beautifulworld.jp.utility.camera.eos7d;

import java.util.ArrayList;
import beautifulworld.jp.utility.camera.*;

public class _MovieProcedure implements Procedure, Callback
 {
  private final int start = 0x04;
  private final int stop = 0;
	
  private Queue usbloop;
  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;
  private int  code;
  
  
  public _MovieProcedure(MessageReceiver receiver, boolean turnon)
   {
	this.receiver = receiver;  
    if (turnon) code = start;
    else code = stop;
	
	replydatas = new ArrayList<ReplyContainer>();
   }

  @Override
  public void startProcedure(Queue q)
   {
	usbloop = q;
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
    changedata.write32(0x10, 0xd1b8);
    changedata.write32(0x14, code);

    q.setControlCode(new ByteArrayWrapper[]{wrapper, changedata}, this);

    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
        if (repcon.replycode == 0x2001)
         {
      	  loop = false;
         }
        repcon.release();
//      	  receiver.debugMes("code 0x9010 replied " + repcon.replycode);
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
