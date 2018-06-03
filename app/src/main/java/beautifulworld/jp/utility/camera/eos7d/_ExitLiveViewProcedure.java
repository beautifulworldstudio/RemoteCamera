package beautifulworld.jp.utility.camera.eos7d;

import beautifulworld.jp.utility.camera.*;

import java.util.ArrayList;

public class _ExitLiveViewProcedure implements Procedure, Callback
 {
  public static final int start = 0x02; 
  private static final int finish = 0x80000000;

  private MessageReceiver receiver;
  private MessageDecoder7D decoder;
  private LiveViewMessageReceiver LVreceiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;
  
  public _ExitLiveViewProcedure(MessageReceiver receiver, LiveViewMessageReceiver LV)
   {
    LVreceiver = LV;
	this.receiver = receiver;
	
    replydatas = new ArrayList<ReplyContainer>();
   }

  @Override
  public void startProcedure(Queue q)
   {
    boolean loop = true;
    int mode = LVreceiver.getLiveViewMode();
  
    if ((mode & Connecter7D.liveviewmode) == 0) return;

    if((mode & Connecter7D.moviemode) == Connecter7D.moviemode)
     {
      ByteArrayWrapper wrapper = ByteArrayManager.getByteWrapper(12);
   	  wrapper.write32(0x00, 12);//パケット長さ
      wrapper.write16(0x04, 1);//カウンタ
      wrapper.write16(0x06, 0x9134);//コード

      q.setControlCode(new ByteArrayWrapper[]{ wrapper }, this);

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
          	  LVreceiver.LiveViewExited();
              receiver.debugMes("Live View Exited.");
             }
           }
          repcon.release();
         }  
        else
         {
    	  //ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
         }
       }//end of while loop
     }//end of if
    else
     {
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
      changedata.write32(0x14, finish);

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
            if (repcon.replycode == 0x2001)
             {
        	  LVreceiver.LiveViewExited();
             }
            }
           repcon.release();
          }  
         else
          {
       	  //ここにreturnを書いてはならない。タイムアウトを設定するのだ!	
          }
        }//end of while loop
      }//end of else
   }

  @Override
  public void debug(String arg){}

  @Override
  public void addData(ReplyContainer reply)
   {
    replydatas.add(reply);
   }
 }
