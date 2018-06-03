package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class _TestProcedure implements Procedure, Callback
 {
  private Queue usbloop;
  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;

  public _TestProcedure(MessageReceiver receiver)
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
 	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write16(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9110);//�R�[�h

    ByteArrayWrapper changedata = ByteArrayManager.getByteWrapper(24);
    changedata.write32(0x00, 24);
    changedata.write16(0x04, 2);
    changedata.write16(0x06, 0x9110);
    changedata.write32(0x0c, 12);
    changedata.write32(0x10, 0xd102);
    changedata.write32(0x14, 0x73);

    q.setControlCode(new ByteArrayWrapper[]{wrapper, changedata}, this);

    while(loop)
     {
  	  if (replydatas.size() != 0)
       {
  		ReplyContainer repcon = null;

  		synchronized(this) { repcon = replydatas.remove(0);	}
       
      	  loop = false;
          repcon.release();
      	  receiver.debugMes("code 0x9010 replied " + repcon.replycode);
       }  
      else
       {
    	//������return�������Ă͂Ȃ�Ȃ��B�^�C���A�E�g��ݒ肷��̂�!	
       }
     }//end of while loop

    wrapper = ByteArrayManager.getByteWrapper(12);
  	wrapper.write32(0x00, 12);//�p�P�b�g����
    wrapper.write8(0x04, 1);//�J�E���^
    wrapper.write16(0x06, 0x9116);//�R�[�h

    while (!q.setControlCode(new ByteArrayWrapper[]{wrapper}, this));
    loop = true;

    while(loop)
     {
      if (replydatas.size() != 0)
       {
    	ReplyContainer rep = null;
        synchronized(this) { rep = replydatas.remove(0); }
         
   	    if (rep.serialnumber == 0x02 ) 
    	 {
          //�f�R�[�h���� 
   	    	ByteArrayWrapper[] data = rep.replydatas;
          if (data != null)
           {
        	byte[] rawdata = data[0].getByteArray();
        	StringBuffer sb = new StringBuffer();
        	sb.append("rawdata = ");
            for(int i = 0; i < rep.packetlength; i++) { sb.append(rawdata[i]); sb.append(":");}
   	    	receiver.debugMes(sb.toString());
           }             

    	 }
    	else if (rep.serialnumber == 0x03)
         {
          loop = false;
         }
        rep.release();
       }
      else
       {
    	//�^�C���A�E�g�ݒ�
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
