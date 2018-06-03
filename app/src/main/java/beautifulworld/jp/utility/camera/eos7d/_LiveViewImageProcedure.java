package beautifulworld.jp.utility.camera.eos7d;

import java.util.ArrayList;
import java.nio.ByteBuffer;
import beautifulworld.jp.utility.camera.*;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

public class _LiveViewImageProcedure implements Procedure, Callback
 {
  private LiveViewMessageReceiver LVreceiver;
  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;

  
  public _LiveViewImageProcedure(MessageReceiver receiver, LiveViewMessageReceiver LVmes)
   {
	this.receiver = receiver;  
    LVreceiver = LVmes;

    replydatas = new ArrayList<ReplyContainer>();
   }

  @Override
  public void startProcedure(Queue q)
   {
    boolean loop = true;
    
    ByteArrayWrapper wrapper = ByteArrayManager.getByteWrapper(16);
 	wrapper.write32(0x00, 16);//パケット長さ
    wrapper.write16(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9153);//コード
    wrapper.write32(0x0c, 0x100000);

    q.setControlCode(new ByteArrayWrapper[]{wrapper }, this);
 
    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}

  		if (repcon.serialnumber == 0x02)
         {
      	  if (repcon.replycode == 0x9153 & repcon.packetlength > 12)
      	   {
       	    try {
       	    ByteBlockWrapper rawdatas = new ByteBlockWrapper(repcon.replydatas);

       		int imagelength = (rawdatas.read(15) << 24) + (rawdatas.read(14) << 16) + (rawdatas.read(13) << 8) + rawdatas.read(12);

       		byte[] imagedata = new byte[imagelength];
       		int pointerD = 0;
            rawdatas.setIndex(20);

            while(true) 
             {
              imagedata[pointerD++] = rawdatas.read();

              if (pointerD >= imagelength) break;
             }
            Bitmap image = BitmapFactory.decodeByteArray(imagedata, 0, imagelength);
            if (image != null ){ LVreceiver.LiveViewImageRetrieved(image); }
      		}
      		catch(Exception e){receiver.debugMes( e.toString() + "at liveviewimage");}
      	   }
      	 }
        if (repcon.serialnumber == 0x03)
         {
       	  loop = false;
          if (repcon.replycode == 0xa102)
           {
        	LVreceiver.LiveViewImageSkipped();  
           }
         }
        repcon.release();
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
