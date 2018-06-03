package beautifulworld.jp.utility.camera.eos7d;

import java.util.ArrayList;
import beautifulworld.jp.utility.camera.*;


public class _CheckProcedure7D implements Procedure, Callback
 {
  private MessageReceiver msgrecv7D;	
  private MessageDecoder7D decoder;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;


  public _CheckProcedure7D(MessageReceiver receiver, MessageDecoder7D decoder)
   {
	this.msgrecv7D = receiver;  
    this.decoder = decoder;

    replydatas = new ArrayList<ReplyContainer>();
   }

  @Override
  public void startProcedure(Queue q)
   {
	ByteArrayWrapper  wrapper = ByteArrayManager.getByteWrapper(12);
  	wrapper.write32(0x00, 12);//パケット長さ
    wrapper.write8(0x04, 1);//カウンタ
    wrapper.write16(0x06, 0x9116);//コード

    while (!q.setControlCode(new ByteArrayWrapper[]{wrapper}, this));

    boolean loop = true;

    while(loop)
     {
      if (replydatas.size() != 0)
       {
    	ReplyContainer rep = null;
        synchronized(this) { rep = replydatas.remove(0); }
	         
   	    if (rep.serialnumber == 0x02 ) 
    	 {
          if (rep.replydatas.length == 1) decoder.decodeMessage(rep.replydatas[0]); //デコードする
          else decoder.decodeMessage(new ByteBlockWrapper(rep.replydatas)); 
   	     }
    	else if (rep.serialnumber == 0x03)
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

    msgrecv7D.checkStatusFinished(); //チェック終了通知
   }

  @Override
  public void debug(String arg){}

  @Override
  public void addData(ReplyContainer reply)
   {
    replydatas.add(reply);
   }
 }
