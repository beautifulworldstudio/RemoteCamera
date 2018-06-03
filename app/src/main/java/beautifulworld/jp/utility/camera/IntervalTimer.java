package beautifulworld.jp.utility.camera;

public class IntervalTimer extends Thread
 {
  private boolean loop;
  private int seconds;
  private long millisec;
  private IntervalTimerReceiver receiver;
  
  public IntervalTimer(IntervalTimerReceiver dest)
   {
    loop = true; 
    receiver = dest;
    seconds = 20;
   }

  public void quitIntervalShoot()
   {
    loop = false;    
   }

  public void setIntervalSecond(int arg)
   {
    if (arg < 5) return;

    seconds = arg;
    millisec = seconds * 1000;  
   }

  public int getInterval()
   {
    return seconds;
   }
  
  @Override
  public void run()
   {
    while(loop)
     {
      try
       {
        receiver.addTimingSignal();
        Thread.sleep(millisec);
       }
      catch(InterruptedException e){ break; }
      catch(NullPointerException e){ break; }
     }
   }

 }
