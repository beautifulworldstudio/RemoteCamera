package beautifulworld.jp.utility.camera.eos7d;

import beautifulworld.jp.utility.camera.*;

import java.util.ArrayList;

public class _StartLiveViewProcedure implements Procedure, Callback
 {
  public static final int code = 0x02; //���C�u�r���[�J�n���̃R�[�h

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
 	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9110);//�R�[�h

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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop
    wrapper =null;
    changedata=null;

    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9110);//�R�[�h

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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop 
    wrapper =null;
    changedata=null;

    //���揀����Ԃɂ���

    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9133);//�R�[�h

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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop 

    /*
    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9110);//�R�[�h

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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop
/*
    wrapper =null;
    changedata=null;
    
    wrapper = ByteArrayManager.getByteWrapper(12);
 	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9116);//�R�[�h

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
          if (repcon.replydatas.length == 1) decoder.decodeMessage(repcon.replydatas[0]); //�f�R�[�h����
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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop 
    wrapper =null;
    changedata=null;
    
    wrapper = ByteArrayManager.getByteWrapper(12);
   	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9101);//�R�[�h

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
      	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop 
    wrapper =null;
    changedata=null;

    wrapper = ByteArrayManager.getByteWrapper(16);
 	wrapper.write32(0x00, 16);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9102);//�R�[�h
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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop
    wrapper =null;
    changedata=null;

    wrapper = ByteArrayManager.getByteWrapper(16);
 	wrapper.write32(0x00, 16);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9102);//�R�[�h
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
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
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
