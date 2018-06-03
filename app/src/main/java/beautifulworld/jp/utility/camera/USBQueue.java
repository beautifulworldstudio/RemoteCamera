package beautifulworld.jp.utility.camera;


import android.hardware.usb.*;
import android.mtp.MtpDevice;
import java.util.ArrayList;

public class USBQueue extends Thread implements Queue
 {
  private boolean loop;
  private UsbDeviceConnection connection;
  private UsbEndpoint send;
  private UsbEndpoint recv;
  private MtpDevice ptpdevice;
  private Callback datareceiver;
  private int counter;
  private int state;
  private ReplyContainer[] repcontainer;
  private ArrayList<ByteArrayWrapper> senddata;

  private MessageReceiver msgrecv;
  
  private static final int idle = 0;
  private static final int sendcode = 1;
  private static final int receive = 2;

  
  public USBQueue(UsbDevice device, UsbManager usbman, MessageReceiver mes) throws IllegalArgumentException
   {
	loop = true;  
    state = idle;
    msgrecv = mes;
    repcontainer = new ReplyContainer[5];

    
    //U����Endpoint���擾����
    UsbInterface usbinterface = device.getInterface(0);

    for(int i = 0; i < usbinterface.getEndpointCount();i++)
     {
      UsbEndpoint ep = usbinterface.getEndpoint(i);

      if (ep.getAddress() == 0x02)//Endpoint(0x02)�̓J����
       {
    	send = ep;
        continue;
       }
      else if (ep.getAddress() == 0x81)//Endpoint(0x81)�̓z�X�g
       {
   	    recv = ep;
        continue;
       }
     }

    if (send == null || recv == null) throw new IllegalArgumentException("Invalid UsbDevice is identified.");
    
    this.connection = usbman.openDevice(device);
    ptpdevice = new MtpDevice(device);
    ptpdevice.open(connection);
   
    senddata = new ArrayList<ByteArrayWrapper>();

    counter = 0x0f;//�J�E���^�[�����l�B���ł�����
   }

  
  //����R�[�h�ƃR�[���o�b�N���Z�b�g����
  @Override
  public synchronized boolean setControlCode(ByteArrayWrapper[] codes, Callback destination)
   {
    if (state != idle) return false;	

    datareceiver = destination;

   //�J�E���^�l����������
    for(ByteArrayWrapper wrapper: codes)
   	 {
      wrapper.write32(0x08, counter);
      senddata.add(wrapper); //�f�[�^�o�^
   	 } 

    state = sendcode;  
    return true;
   }
  
  public int getPacketSize()
   {
	return recv.getMaxPacketSize();  
   }


  //�L���[�J�n
  @Override
  public void startLoop()
   {
	start();  
   }

  //�L���[��~
  @Override
  public void stopLoop()
   {
	loop = false;
   }
 
  @Override
  public void close()
   {
	connection.close();  
   }

  //�X���b�h�{��
  @Override
  public void run()
   {
	ArrayList<ByteArrayWrapper> extentionBuffer = new ArrayList<ByteArrayWrapper>();

	  while(loop)
	 {
	  synchronized(this)
	    {
        if (state == sendcode)
         {
          while(senddata.size() != 0)
           {
            ByteArrayWrapper wrapper = senddata.remove(0);
         
            connection.bulkTransfer(send, wrapper.getByteArray(), wrapper.getDataLength(), 0);
            ByteArrayManager.release(wrapper);//���M���I������̂ŉ���B
           }
           state = receive;
          }
        else if (state == receive)
         {
       	  ByteArrayWrapper wrapper = ByteArrayManager.getByteWrapper(0x200);
          byte[] base = wrapper.getByteArray();
          int read = connection.bulkTransfer(recv, base, 0x200, 0);
          ReplyContainer reply = new ReplyContainer();

      	  if (read > 0)
      	   {
   		    wrapper.setDataLength(read);
   		    
   	        reply.packetlength = ((base[3] & 0xff) << 24) + ((base[2] & 0xff) << 16) + ((base[1] & 0xff)<< 8) + (base[0] & 0xff);	
   	        reply.serialnumber = ((base[5] & 0xff) << 8) + (base[4] & 0xff);
   	        reply.replycode = ((base[7] & 0xff) << 8) + (base[6] & 0xff);
            int number = ((((((base[11] & 0xff) << 8) + (base[10] & 0xff)) << 8) + (base[9] & 0xff))<< 8) + (base[8] & 0xff);	
            
            if (reply.packetlength > read )//�ǂݍ��݃f�[�^��512�o�C�g�𒴂���ꍇ
             {
              int extendlength = reply.packetlength - read;
              extentionBuffer.add(wrapper);

              while(extendlength > 0)
               {
             	ByteArrayWrapper packet = ByteArrayManager.getByteWrapper(16384);//16k�o�C�g�m��
                byte[] buf = packet.getByteArray();
  
                int read2 = connection.bulkTransfer(recv, buf, 16384, 0);
                packet.setDataLength(read2);
                extentionBuffer.add(packet);
                extendlength -= read2;
               }

   	          reply.replydatas = new ByteArrayWrapper[extentionBuffer.size()];
              for(int i = 0; i < reply.replydatas.length; i++)
               {
           	    reply.replydatas[i] = extentionBuffer.remove(0);
               }
             }
           else
             {
           	  reply.replydatas = new ByteArrayWrapper[]{ wrapper };
             }
            datareceiver.addData(reply);

            if (reply.serialnumber == 3)
             {
              state = idle;
        	  datareceiver = null;
              counter++;
             } 
           }
         }//end of if block
	   }
	 }
   }
 }
