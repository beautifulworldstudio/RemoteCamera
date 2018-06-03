package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class _ChkStatusProcedure implements Procedure, Callback
 {
  private Queue usbloop;
  private MessageReceiver receiver;
  private ArrayList<ReplyContainer> replydatas;
  private ReplyContainer[] container;

  public _ChkStatusProcedure()
   {
	replydatas = new ArrayList<ReplyContainer>();
   }

  @Override
  public void startProcedure(Queue q)
   {
	usbloop = q;
   }

  @Override
  public void debug(String arg){}

  @Override
  public void addData(ReplyContainer reply){}
 }
