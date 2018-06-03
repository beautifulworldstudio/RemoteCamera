package beautifulworld.jp.utility.camera;

import java.util.ArrayList;

public class ProcedureLoop extends Thread implements ProcedureHandler
 {
  private ArrayList<Procedure> waitinglist;
  private Queue argument;
  private boolean loop;
  
  public ProcedureLoop(Queue q)
   {
	waitinglist = new ArrayList<Procedure>();  
    argument = q;
    loop = true;
   }

 
  @Override
  public void addProcedure(Procedure p)
   {
	synchronized(waitinglist)
	 {
	  waitinglist.add(p);  
     }
   }

 
 
  @Override
  public void run()
   {
	while(loop)
	 {
	  if (waitinglist.size() != 0)
	   {
		Procedure p = null;  

		synchronized(this){ p = waitinglist.remove(0); }
		
	    if (p != null) p.startProcedure(argument);
	   }
     }
   }
  
  //ÉãÅ[Éví‚é~
  public void stopLoop()
   {
	loop = false;  
   }
 }
