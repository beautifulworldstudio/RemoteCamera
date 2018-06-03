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

  //必須プロシージャ
  private _CheckProcedure7D checkProc; //ステータス取得プロシージャ
  private _LiveViewImageProcedure lvProc; //ライブビューイメージ取得
  private ImageView liveview;

  //タイマー関連 
  private ScheduledThreadPoolExecutor timer;
  private long checkIntervalTextMode;
  //private long checkIntervalLiveMode;

  //インターバル撮影関連
  private int intervalsecond = 20;
  private IntervalTimer intervaltimer;

  private int applicationmode;

  private Handler h = new Handler(); //UI変更用ハンドラー


  private Connecter7D(){} //勝手にインスタンス化するな!

  
  //初期化メソッド	
  public static Connecter7D newInstance(Activity context, UsbDevice target)
   {
    Connecter7D result = new Connecter7D();  

    result.usbdevice = target;					//USBデバイスを格納
    result.ptpdevice = new MtpDevice(target);	//MTPデバイスを取得

    if( result.ptpdevice == null) return null;

    result.params7D = new EOS7DParameters();	//情報保持クラス生成
    result.UsbQueue = new USBQueue(target, ((UsbManager)context.getSystemService(Context.USB_SERVICE)), result);  //USbとの通信用キュー
    result.procLoop = new ProcedureLoop(result.UsbQueue); //Procedure用ループ
    result.decoder = new MessageDecoder7D(result);
    result.checkProc = new _CheckProcedure7D(result, result.decoder); //チェックプロシージャ

    result.timer = new ScheduledThreadPoolExecutor(2); //タイマー
    result.checkIntervalTextMode = 600L; //チェックのインターバル

    ByteArrayManager.initialize(); //ByteArrayManager初期化
    
    result.applicationmode = textmode | localmode;
    
    return result;
   }


  @Override
  public void setActivity(Activity act)
   {
	if (act == null) return;  

	
	parent = act;
	   //Activityのレイアウトを初期化
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
    act.setContentView(FL); //レイアウトをセット

    
    //UIのイベント設定
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
   //UIのイベント設定
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

  //serviceセット
  @Override
  public void setService(PortableControllerService ser)
   {
	bgservice = ser;
   }
  
  //Activity~呼び出されるスタート用メソッド
  @Override
  public void connect()
   {
	UsbQueue.startLoop(); //キューのスレッドをスタート
	procLoop.start(); //プロシージャループのスレッドをスタート

    //初期化プロシージャをセットする
    _InitializeProcedure initProc = new _InitializeProcedure(this);
    addProcedure(initProc);
   }

  
  //Activityから接続を切断する指示を受け取る
  @Override
  public void disconnect()
   {
    timer.shutdown();//タイマー停止

    addProcedure(new _FinalizeProcedure(this)); //通信終了のプロシージャをセット
   }
  
  
  //ProcedureLoop(ProcedureHandler)へ転送する
  @Override
  public void addProcedure(Procedure p)
   {
	procLoop.addProcedure(p);
   }

   
  //イベントリスナー
  @Override
  public void onClick(View v)
   {
	if (parent == null) return ;

	if (parent.findViewById(R.id.quitbutton).equals(v))//切断のプロシージャをセットする
 	 {
      timer.shutdown();//タイマー停止

      addProcedure(new _FinalizeProcedure(this)); //通信終了のプロシージャをセット

      //parent.finish();//Activityを終了 
     }
	else if (parent.findViewById(R.id.shutterbutton).equals(v))
	 {
      addProcedure(new _PushShutterProcedure(this));
	  //addProcedure(new _StartLiveViewProcedure(this, this, decoder));
	 }
    else if (parent.findViewById(R.id.StartInterval).equals(v)) startIntervalShooting();
    else if (parent.findViewById(R.id.StopInterval).equals(v)) stopIntervalShooting();
    //インターバル変更
    else if(parent.findViewById(R.id.IntervalPlus).equals(v))  changeInterval(true);
    else if(parent.findViewById(R.id.IntervalMinus).equals(v)) changeInterval(false);

    else if (parent.findViewById(R.id.LVoff).equals(v))
	 {
     //addProcedure(new _PushShutterProcedure(this));
	  //if (lvProc != null) addProcedure(lvProc);

	  //else addProcedure(new _LiveViewImageProcedure(this, this));
	 }
    //シャッタースピード変更
	else if(parent.findViewById(R.id.SSPlus).equals(v))
	 {
      Sequences.changeShutterSpeed(params7D, procLoop, true);//テスト
	 }
	else if(parent.findViewById(R.id.SSMinus).equals(v))
	 {
	  Sequences.changeShutterSpeed(params7D, procLoop, false);
	 }
    //露出変更
	else if(parent.findViewById(R.id.AperturePlus).equals(v))
     {
      Sequences.changeAperture(params7D, procLoop, true);//テスト
     }
    else if(parent.findViewById(R.id.ApertureMinus).equals(v))
     {
      Sequences.changeAperture(params7D, procLoop, false);
     }
    //ISO変更
    else if(parent.findViewById(R.id.ISOPlus).equals(v))
     {
      Sequences.changeISO(params7D, procLoop, true);//テスト
     }
    else if(parent.findViewById(R.id.ISOMinus).equals(v))
     {
      Sequences.changeISO(params7D, procLoop, false);
     }
    //ドライブモード変更
    else if(parent.findViewById(R.id.DriveModePlus).equals(v))
     {
      Sequences.changeDriveMode(params7D, procLoop, true);//テスト
     }
    else if(parent.findViewById(R.id.DriveModeMinus).equals(v))
     {
      Sequences.changeDriveMode(params7D, procLoop, false);
     }
    //測光変更
    else if(parent.findViewById(R.id.MeteringModePlus).equals(v))
     {
      Sequences.changeMeteringMode(params7D, procLoop, true);//テスト
     }
    else if(parent.findViewById(R.id.MeteringModeMinus).equals(v))
     {
      Sequences.changeMeteringMode(params7D, procLoop, false);
     }
	//AFモード変更
    else if(parent.findViewById(R.id.AFModePlus).equals(v))
     {
      Sequences.changeAFMode(params7D, procLoop, true);//テスト
     }
    else if(parent.findViewById(R.id.AFModeMinus).equals(v))
     {
      Sequences.changeAFMode(params7D, procLoop, false);
     }
	//ホワイトバランス変更
    else if(parent.findViewById(R.id.WhiteBalancePlus).equals(v))
     {
      Sequences.changeWhiteBalance(params7D, procLoop, true);//テスト
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
  
  
  //インターバル撮影
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
  
  //インターバルを変更する
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

  //インターバルタイマーからの信号を受け取る
  @Override
  public void addTimingSignal()
   {
    //シャッターを切る
    addProcedure(new _PushShutterProcedure(this));
   }

  
  //EOSからのメッセージを受け取る
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

  
  //通信を切断した跡に呼び出される
  @Override
  public void connectionFinalized()
   {
    procLoop.stopLoop();  //プロシージャループ停止
    UsbQueue.stopLoop();  //キュー停止  

    ptpdevice.close();  //MtpDeviceクローズ
    UsbQueue.close();

    removeActivity(parent);
    bgservice.connecterStopped();

    //サービスへの参照を消去
    bgservice = null;
   }

  //システムチェック終了時に呼び出される
  @Override
  public void checkStatusFinished()
   {
    //次のチェックを指定する。
	//一定時間ごとに呼び出して前のチェックが終わっているか調べるよりはこの方法が容易

    //インターバルを指定
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

  //ライブビュー開始
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

  //ライブビューイメージ取得
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

  //次のイメージの読み込みをセット
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
	//activityがされているか判定せよ  
	  
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


  //プロパティ値を再描画
  private void rewriteIntervalTime()
   {
    final String arg = String.valueOf(intervalsecond);

    h.post(new Runnable()
    {
     @Override
     public void run()
      {
       //UI変更
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
       //UI変更
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
        //UI変更
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
	    //UI変更
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
	    //UI変更
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
        //UI変更
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
        //UI変更
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
        //UI変更
        ((TextView)parent.findViewById(R.id.WhiteBalanceValue)).setText(params7D.getCurrentWhiteBalance());
       }
     });
   }
 }
