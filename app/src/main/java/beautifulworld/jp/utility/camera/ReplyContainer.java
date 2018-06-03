package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class ReplyContainer
 {
  public int packetlength = 0;
  public int serialnumber = 0;
  public int replycode = 0;
  public ByteArrayWrapper[] replydatas;
  
  public void release()
   {
	if (replydatas == null) return;
	
	for(ByteArrayWrapper wrapper: replydatas)
	 {
	  ByteArrayManager.release(wrapper);	
	 }  
   }
 }
