package beautifulworld.jp.utility.camera.eos7d;

import android.app.Activity;
import android.app.Service;
import android.os.Handler;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.graphics.Bitmap;
import android.mtp.MtpDevice;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.LayoutInflater;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import beautifulworld.jp.utility.camera.*;



public final class Connecter7D implements  EOSConnecter , ProcedureHandler, MessageReceiver, LiveViewMessageReceiver,
View.OnClickListener, IntervalTimerReceiver
 {
  public static final int localmode = 0x00;
  public static final int remotemode = 0x01;
  public static final int textmode = 0x02;  	
  public static final int liveviewmode = 0x04;
  public static final int moviemode = 0x08;
	
  //private Service background;

  private Activity	parent;
  private PortableControllerService bgservice;
  private UsbDevice usbdevice;
  private MtpDevice ptpdevice;
  private ProcedureLoop procLoop;
  private EOS7DParameters params7D;
  private Queue UsbQueue;
  private MessageDecoder7D decoder;

  //�K�{�v���V�[�W��
  private _CheckProcedure7D checkProc; //�X�e�[�^�X�擾�v���V�[�W��
  private _LiveViewImageProcedure lvProc; //���C�u�r���[�C���[�W�擾
  private ImageView liveview;

  //�^�C�}�[�֘A 
  private ScheduledThreadPoolExecutor timer;
  private long checkIntervalTextMode;
  //private long checkIntervalLiveMode;

  //�C���^�[�o���B�e�֘A
  private int intervalsecond = 20;
  private IntervalTimer intervaltimer;

  private int applicationmode;

  private Handler h = new Handler(); //UI�ύX�p�n���h���[


  private Connecter7D(){} //����ɃC���X�^���X�������!

  
  //���������\�b�h	
  public static Connecter7D newInstance(Activity context, UsbDevice target)
   {
    Connecter7D result = new Connecter7D();  

    result.usbdevice = target;					//USB�f�o�C�X���i�[
    result.ptpdevice = new MtpDevice(target);	//MTP�f�o�C�X���擾

    if( result.ptpdevice == null) return null;

    result.params7D = new EOS7DParameters();	//���ێ��N���X����
    result.UsbQueue = new USBQueue(target, ((UsbManager)context.getSystemService(Context.USB_SERVICE)), result);  //USb�Ƃ̒ʐM�p�L���[
    result.procLoop = new ProcedureLoop(result.UsbQueue); //Procedure�p���[�v
    result.decoder = new MessageDecoder7D(result);
    result.checkProc = new _CheckProcedure7D(result, result.decoder); //�`�F�b�N�v���V�[�W��

    result.timer = new ScheduledThreadPoolExecutor(2); //�^�C�}�[
    result.checkIntervalTextMode = 600L; //�`�F�b�N�̃C���^�[�o��

    ByteArrayManager.initialize(); //ByteArrayManager������
    
    result.applicationmode = textmode | localmode;
    
    return result;
   }


  @Override
  public void setActivity(Activity act)
   {
	if (act == null) return;  

	
	parent = act;
	   //Activity�̃��C�A�E�g��������
    FrameLayout FL = new FrameLayout(act);
    LayoutInflater inflater = LayoutInflater.from(act);
    View consoleview = inflater.inflate(R.layout.eos7d, null);

    liveview = new ImageView(act);
//    ViewGroup.LayoutParams params = result.liveview.getLayoutParams();
//    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
    FL.addView(liveview);

    FL.addView(consoleview);
    FL.setVisibility(View.VISIBLE);
    act.setContentView(FL); //���C�A�E�g���Z�b�g

    
    //UI�̃C�x���g�ݒ�
    try
     {
      ((Button)act.findViewById(R.id.quitbutton)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.SSPlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.SSMinus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.AperturePlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.ApertureMinus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.ISOPlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.ISOMinus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.DriveModePlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.DriveModeMinus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.MeteringModePlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.MeteringModeMinus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.AFModePlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.AFModeMinus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.WhiteBalancePlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.WhiteBalanceMinus)).setOnClickListener(this);

      ((Button)act.findViewById(R.id.StartInterval)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.StopInterval)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.IntervalPlus)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.IntervalMinus)).setOnClickListener(this);

      ((Button)act.findViewById(R.id.shutterbutton)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.LVoff)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.LVStart)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.LVStop)).setOnClickListener(this);
      ((Button)act.findViewById(R.id.LVExit)).setOnClickListener(this);
      }
    catch(Exception e){}

    rewriteShutterSpeed();
    rewriteISO();
    rewriteAperture();
    rewriteDriveMode();
    rewriteMeteringMode();
    rewriteAFMode();
    rewriteWhiteBalance();
    rewriteIntervalTime();
   }

  
  @Override
  public void removeActivity(Activity act)
   {
   //UI�̃C�x���g�ݒ�
    try
     {
      ((Button)act.findViewById(R.id.quitbutton)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.SSPlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.SSMinus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.AperturePlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.ApertureMinus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.ISOPlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.ISOMinus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.DriveModePlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.DriveModeMinus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.MeteringModePlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.MeteringModeMinus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.AFModePlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.AFModeMinus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.WhiteBalancePlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.WhiteBalanceMinus)).setOnClickListener(null);
      
      ((Button)act.findViewById(R.id.StartInterval)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.StopInterval)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.IntervalPlus)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.IntervalMinus)).setOnClickListener(null);

      ((Button)act.findViewById(R.id.shutterbutton)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.LVoff)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.LVStart)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.LVStop)).setOnClickListener(null);
      ((Button)act.findViewById(R.id.LVExit)).setOnClickListener(null);
      }
    catch(Exception e){}
   } 

  //service�Z�b�g
  @Override
  public void setService(PortableControllerService ser)
   {
	bgservice = ser;
   }
  
  //Activity~�Ăяo�����X�^�[�g�p���\�b�h
  @Override
  public void connect()
   {
	UsbQueue.startLoop(); //�L���[�̃X���b�h���X�^�[�g
	procLoop.start(); //�v���V�[�W�����[�v�̃X���b�h���X�^�[�g

    //�������v���V�[�W�����Z�b�g����
    _InitializeProcedure initProc = new _InitializeProcedure(this);
    addProcedure(initProc);
   }

  
  //Activity����ڑ���ؒf����w�����󂯎��
  @Override
  public void disconnect()
   {
    timer.shutdown();//�^�C�}�[��~

    addProcedure(new _FinalizeProcedure(this)); //�ʐM�I���̃v���V�[�W�����Z�b�g
   }
  
  
  //ProcedureLoop(ProcedureHandler)�֓]������
  @Override
  public void addProcedure(Procedure p)
   {
	procLoop.addProcedure(p);
   }

   
  //�C�x���g���X�i�[
  @Override
  public void onClick(View v)
   {
	if (parent == null) return ;

	if (parent.findViewById(R.id.quitbutton).equals(v))//�ؒf�̃v���V�[�W�����Z�b�g����
 	 {
      timer.shutdown();//�^�C�}�[��~

      addProcedure(new _FinalizeProcedure(this)); //�ʐM�I���̃v���V�[�W�����Z�b�g

      //parent.finish();//Activity���I�� 
     }
	else if (parent.findViewById(R.id.shutterbutton).equals(v))
	 {
      addProcedure(new _PushShutterProcedure(this));
	  //addProcedure(new _StartLiveViewProcedure(this, this, decoder));
	 }
    else if (parent.findViewById(R.id.StartInterval).equals(v)) startIntervalShooting();
    else if (parent.findViewById(R.id.StopInterval).equals(v)) stopIntervalShooting();
    //�C���^�[�o���ύX
    else if(parent.findViewById(R.id.IntervalPlus).equals(v))  changeInterval(true);
    else if(parent.findViewById(R.id.IntervalMinus).equals(v)) changeInterval(false);

    else if (parent.findViewById(R.id.LVoff).equals(v))
	 {
     //addProcedure(new _PushShutterProcedure(this));
	  //if (lvProc != null) addProcedure(lvProc);

	  //else addProcedure(new _LiveViewImageProcedure(this, this));
	 }
    //�V���b�^�[�X�s�[�h�ύX
	else if(parent.findViewById(R.id.SSPlus).equals(v))
	 {
      Sequences.changeShutterSpeed(params7D, procLoop, true);//�e�X�g
	 }
	else if(parent.findViewById(R.id.SSMinus).equals(v))
	 {
	  Sequences.changeShutterSpeed(params7D, procLoop, false);
	 }
    //�I�o�ύX
	else if(parent.findViewById(R.id.AperturePlus).equals(v))
     {
      Sequences.changeAperture(params7D, procLoop, true);//�e�X�g
     }
    else if(parent.findViewById(R.id.ApertureMinus).equals(v))
     {
      Sequences.changeAperture(params7D, procLoop, false);
     }
    //ISO�ύX
    else if(parent.findViewById(R.id.ISOPlus).equals(v))
     {
      Sequences.changeISO(params7D, procLoop, true);//�e�X�g
     }
    else if(parent.findViewById(R.id.ISOMinus).equals(v))
     {
      Sequences.changeISO(params7D, procLoop, false);
     }
    //�h���C�u���[�h�ύX
    else if(parent.findViewById(R.id.DriveModePlus).equals(v))
     {
      Sequences.changeDriveMode(params7D, procLoop, true);//�e�X�g
     }
    else if(parent.findViewById(R.id.DriveModeMinus).equals(v))
     {
      Sequences.changeDriveMode(params7D, procLoop, false);
     }
    //�����ύX
    else if(parent.findViewById(R.id.MeteringModePlus).equals(v))
     {
      Sequences.changeMeteringMode(params7D, procLoop, true);//�e�X�g
     }
    else if(parent.findViewById(R.id.MeteringModeMinus).equals(v))
     {
      Sequences.changeMeteringMode(params7D, procLoop, false);
     }
	//AF���[�h�ύX
    else if(parent.findViewById(R.id.AFModePlus).equals(v))
     {
      Sequences.changeAFMode(params7D, procLoop, true);//�e�X�g
     }
    else if(parent.findViewById(R.id.AFModeMinus).equals(v))
     {
      Sequences.changeAFMode(params7D, procLoop, false);
     }
	//�z���C�g�o�����X�ύX
    else if(parent.findViewById(R.id.WhiteBalancePlus).equals(v))
     {
      Sequences.changeWhiteBalance(params7D, procLoop, true);//�e�X�g
     }
    else if(parent.findViewById(R.id.WhiteBalanceMinus).equals(v))
     {
      Sequences.changeWhiteBalance(params7D, procLoop, false);
     }

    else if(parent.findViewById(R.id.LVStart).equals(v))
	 {
	  addProcedure(new _MovieProcedure(this, true));
	 }
	else if(parent.findViewById(R.id.LVStop).equals(v))
	 {
	  addProcedure(new _MovieProcedure(this, false));
	 }
	else if(parent.findViewById(R.id.LVExit).equals(v))
	 {
	  addProcedure(new _ExitLiveViewProcedure(this, this));
	 }
   }
  
  
  //�C���^�[�o���B�e
  private void startIntervalShooting()
   {
    parent.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        
    
    intervaltimer = new IntervalTimer(this); 
    intervaltimer.setIntervalSecond(intervalsecond);
    intervaltimer.start();
   }

  private void stopIntervalShooting()
   {
    parent.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
    intervaltimer.quitIntervalShoot();
   }
  
  //�C���^�[�o����ύX����
  private void changeInterval(boolean isPlus)
   {
    if (isPlus)
    intervalsecond += 5;
    else
     {
      intervalsecond -= 5;
      if (intervalsecond <= 0) intervalsecond = 5;
     }

    if (intervaltimer != null) intervaltimer.setIntervalSecond(intervalsecond);

    try{
      if (parent != null) rewriteIntervalTime();
      
    }
    catch(Exception e)
     {
      debugMes(e.toString());
     }
   }

  public int getIntervalTime()
   {
    return intervalsecond; 
   }

  //�C���^�[�o���^�C�}�[����̐M�����󂯎��
  @Override
  public void addTimingSignal()
   {
    //�V���b�^�[��؂�
    addProcedure(new _PushShutterProcedure(this));
   }

  
  //EOS����̃��b�Z�[�W���󂯎��
  @Override
  public synchronized void debugMes(String arg)
   {
 	final String s = arg;
	 	  
	h.post(new Runnable()
     {
       @Override
      public void run()
       {
    	TextView tv = (TextView)parent.findViewById(R.id.state);
        tv.setText(s);
       }
     }
    );
   }  

  @Override
  public void connectionInitialized()
   {
//	debugMes("Initialized packetsize " + UsbQueue.getPacketSize());

	timer.execute(new Runnable()
     {
      @Override
      public void run()
       {
    	procLoop.addProcedure(checkProc);
       }
     });
   }

  
  //�ʐM��ؒf�����ՂɌĂяo�����
  @Override
  public void connectionFinalized()
   {
    procLoop.stopLoop();  //�v���V�[�W�����[�v��~
    UsbQueue.stopLoop();  //�L���[��~  

    ptpdevice.close();  //MtpDevice�N���[�Y
    UsbQueue.close();

    removeActivity(parent);
    bgservice.connecterStopped();

    //�T�[�r�X�ւ̎Q�Ƃ�����
    bgservice = null;
   }

  //�V�X�e���`�F�b�N�I�����ɌĂяo�����
  @Override
  public void checkStatusFinished()
   {
    //���̃`�F�b�N���w�肷��B
	//��莞�Ԃ��ƂɌĂяo���đO�̃`�F�b�N���I����Ă��邩���ׂ���͂��̕��@���e��

    //�C���^�[�o�����w��
	long interval = checkIntervalTextMode;

    timer.schedule(new Runnable()
	 {
	  @Override
	  public void run()
	   {
	  	procLoop.addProcedure(checkProc);
	   }
	 }, interval, TimeUnit.MILLISECONDS);
   } 

  //���C�u�r���[�J�n
  @Override
  public synchronized void LiveViewStarted()
   {
	if (lvProc == null) lvProc = new _LiveViewImageProcedure(this, this);
    final _LiveViewImageProcedure p = lvProc;	  

    if ((applicationmode & textmode) == textmode) applicationmode ^= textmode;
    applicationmode |= liveviewmode;

    timer.schedule(new Runnable()
	 {
	  @Override
	  public void run()
	   {
	  	procLoop.addProcedure(p);
	   }
	 }, 500L, TimeUnit.MILLISECONDS);
   }

  @Override
  public synchronized void MovieModeStarted()
   {
    applicationmode |= moviemode;

    if ((applicationmode & liveviewmode) != liveviewmode)
	 {
      LiveViewStarted();
	 }
   }

  //���C�u�r���[�C���[�W�擾
  @Override
  public synchronized void LiveViewImageRetrieved(Bitmap image)
   {
    final Bitmap btm = image;

    h.post(new Runnable()
     {
      @Override
      public void run()
       {
    	liveview.setImageBitmap(btm);
       }
     });
    nextImageReading();
   }

  @Override
  public void LiveViewImageSkipped()
   {
	nextImageReading();
   }

  //���̃C���[�W�̓ǂݍ��݂��Z�b�g
  private void nextImageReading()
   {
	final _LiveViewImageProcedure p = lvProc;

    if ((applicationmode & liveviewmode) == liveviewmode)
     {
      timer.schedule(new Runnable()
       {
        @Override
    	public void run()
    	 {
    	  procLoop.addProcedure(p);
    	 }
       }, 200L, TimeUnit.MILLISECONDS); 
     }
   }

  @Override
  public int getLiveViewMode()
   {
    int mask = liveviewmode + moviemode;

    return applicationmode & mask;  
   }
  
  
  @Override
  public synchronized void LiveViewExited()
   {
	if ((applicationmode & liveviewmode) == liveviewmode) applicationmode ^= liveviewmode;
	if ((applicationmode & moviemode) == moviemode) applicationmode ^= moviemode;
   }

  
  @Override
  public void changeShutterSpeed(int code)
   {
	//activity������Ă��邩���肹��  
	  
    if (params7D.setShutterSpeed(code) && parent != null) rewriteShutterSpeed();
   }

  @Override
  public void changeISO(int code)
   {
    if (params7D.setISO(code) && parent != null) rewriteISO();
   }
  
  @Override
  public void changeAperture(int code)
   {
	if (params7D.setAperture(code) && parent != null) rewriteAperture();
   }

  @Override
  public void changeDriveMode(int code)
   {
	if (params7D.setDriveMode(code) && parent != null) rewriteDriveMode();
   } //0xD106

  @Override
  public void changeMeteringMode(int code)
   {
    if (params7D.setMeteringMode(code) && parent != null) rewriteMeteringMode();
   }

  @Override
  public void changeAFMode(int code)
   {
    if (params7D.setAFMode(code) && parent != null) rewriteAFMode();
   }

  @Override
  public void changeWhiteBalance(int code)
   {
    if (params7D.setWhiteBalance(code) && parent != null) rewriteWhiteBalance();
   }


  //�v���p�e�B�l���ĕ`��
  private void rewriteIntervalTime()
   {
    final String arg = String.valueOf(intervalsecond);

    h.post(new Runnable()
    {
     @Override
     public void run()
      {
       //UI�ύX
       TextView tv = ((TextView)parent.findViewById(R.id.IntervalValue));
       tv.setText(arg);
      }
    });  
   }
  
  private void rewriteShutterSpeed()
   {
   	h.post(new Runnable()
     {
      @Override
      public void run()
       {
       //UI�ύX
    	((TextView)parent.findViewById(R.id.SSValue)).setText(params7D.getCurrentShutterSpeed());
       }
     });  
   }
  
  private void rewriteISO()
   {
   	h.post(new Runnable()
     {
      @Override
      public void run()
       {
        //UI�ύX
        ((TextView)parent.findViewById(R.id.ISOValue)).setText(params7D.getCurrentISO());
       }
     });
   }

  private void rewriteAperture()
   {
	h.post(new Runnable()
	 {
	  @Override
	  public void run()
	   {
	    //UI�ύX
	    ((TextView)parent.findViewById(R.id.ApertureValue)).setText(params7D.getCurrentAperture());
	   }
	 });  
   }

  private void rewriteDriveMode()
   {
	h.post(new Runnable()
	 {
	  @Override
	  public void run()
	   {
	    //UI�ύX
	    ((TextView)parent.findViewById(R.id.DriveModeValue)).setText(params7D.getCurrentDriveMode());
	   }
	 });
   }

  private void rewriteMeteringMode()
   {
    h.post(new Runnable()
     {
      @Override
      public void run()
       {
        //UI�ύX
        ((TextView)parent.findViewById(R.id.MeteringModeValue)).setText(params7D.getCurrentMeteringMode());
       }
     });
   }

  private void rewriteAFMode()
   {
   	h.post(new Runnable()
     {
      @Override
      public void run()
       {
        //UI�ύX
        ((TextView)parent.findViewById(R.id.AFModeValue)).setText(params7D.getCurrentAFMode());
       }
     });
   }

  private void rewriteWhiteBalance()
   {
    h.post(new Runnable()
     {
      @Override
      public void run()
       {
        //UI�ύX
        ((TextView)parent.findViewById(R.id.WhiteBalanceValue)).setText(params7D.getCurrentWhiteBalance());
       }
     });
   }
 }
