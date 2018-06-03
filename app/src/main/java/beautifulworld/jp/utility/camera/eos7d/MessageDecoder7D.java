package beautifulworld.jp.utility.camera.eos7d;

import beautifulworld.jp.utility.camera.ByteArrayWrapper;
import beautifulworld.jp.utility.camera.ByteBlockWrapper;
import beautifulworld.jp.utility.camera.ByteArrayContainer;
import beautifulworld.jp.utility.camera.MessageReceiver;


public class MessageDecoder7D
 {
  private MessageReceiver msgrecv7D;
  

  public MessageDecoder7D(MessageReceiver connecter)
   {
    msgrecv7D = connecter;
   }

  //メッセージデコード　
  public void decodeMessage(ByteArrayContainer datas)
   {
    if (datas == null || datas.getDataLength() == 0) return;
     
     //初期値をMTPヘッダの次の位置にセット
    int addressindex = 12;

    while(true)
     {
      try
       {
    	int msglength = 
    	datas.read(addressindex++) +
    	(datas.read(addressindex++) << 8) +
    	(datas.read(addressindex++) << 16) +
    	(datas.read(addressindex++) << 24) - 4;
      
        int[] segment = new int[msglength];

        //バイト列をintとして読み込み
        for(int i = 0; i < msglength; i++) segment[i] = datas.read(addressindex++);

        if (msglength == 4 & segment[0] == 0) break; //終了コード
        else codeswitch(segment);
       }
      catch(Exception e){ break; }
     }
   }
  
  private void codeswitch(int[] datasegment)
   {
	int code = (datasegment[1] << 8) + datasegment[0];

	if (code == 0xc189)
	 {
	  int function = (datasegment[5] << 8) + datasegment[4];

	  switch (function)
	   {
	    case 0xd101: msgrecv7D.changeAperture(datasegment[8]); break;
	    case 0xd102: msgrecv7D.changeShutterSpeed(datasegment[8]); break;
	    case 0xd103: msgrecv7D.changeISO(datasegment[8]); break;
	    case 0xd106: msgrecv7D.changeDriveMode(datasegment[8]); break;
	    case 0xd107: msgrecv7D.changeMeteringMode(datasegment[8]); break;
	    case 0xd108: msgrecv7D.changeAFMode(datasegment[8]); break;
	    case 0xd109: msgrecv7D.changeWhiteBalance(datasegment[8]); break;
//	    case 0xd1b0: msgrecv7D.debugMes("val =" + datasegment[8] +" :"+ datasegment[11]);break;
	   }
     }
  }
 }
