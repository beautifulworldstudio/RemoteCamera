package beautifulworld.jp.utility.camera;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

public class ByteArrayManager
 {
  private static ByteArrayManager instance;	
	
  private static final int low = 512; //低容量の上限サイズ
  private static final int medium = 10240; //中容量
  private static final int large = 51200; //高容量
  private static final int huge = 4096000; //大容量400k

  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> lowsize;
  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> mediumsize;
  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> largesize;
  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> hugesize;
  

  private ByteArrayManager(){}//外部クラスのインスタンス化禁止
  
  //ByteArrayManagerを初期化
  public static void initialize()
   {
	if (instance != null) return;
	
	instance = new ByteArrayManager();
	instance.lowsize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
	instance.mediumsize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
	instance.largesize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
	instance.hugesize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
   }
 
  //バイト列を返す
  public static ByteArrayWrapper getByteWrapper(int length)
   {
    if (length <= 0)  return null;

    synchronized(instance)
     {
      HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> target = instance.lowsize;
      int allocatesize = low;
    
      if (length > low & length <= medium)
       {
        target = instance.mediumsize;
        allocatesize = medium;
       }
      else if (length > medium & length <= large)
       {
        target = instance.largesize;
        allocatesize = large;
       }
      else if (length > large & length <= huge)
       {
        target = instance.hugesize;
        allocatesize = huge;
       }
      else if (length > huge) return null; 
 
      Iterator<ByteArrayWrapper.accessor> iterate = target.values().iterator();

      while(iterate.hasNext())
       {
   	    ByteArrayWrapper.accessor inner = iterate.next();

        if (inner.getFlag())//未使用の場合
         {
          inner.setFlag(false); //使用状態に変更
          ByteArrayWrapper result = inner.getOuterInstance();
          result.setDataLength(length);
          return result;
         }
       }
    
    //すべて使用中の場合は新規に生成する。
      ByteArrayWrapper.accessor inner = ByteArrayWrapper.newInstance(allocatesize);
      ByteArrayWrapper result = inner.getOuterInstance();
      inner.setFlag(false);//使用状態に変更
      result.setDataLength(length);//実際に使用するデータ容量を指定(必須)
      
      target.put(result, inner);    

      return result;
     }
   } 

  
   //バイト列をリリースする。
  public static void release(ByteArrayWrapper nouse)
   {
    synchronized(instance)
     {
	  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> target = null; 

	  if (instance.lowsize.containsKey(nouse)) target = instance.lowsize;
	  else if(instance.mediumsize.containsKey(nouse)) target = instance.mediumsize;
	  else if(instance.largesize.containsKey(nouse)) target = instance.largesize;
	  else if(instance.hugesize.containsKey(nouse)) target = instance.hugesize;
	  else return;

  	  ByteArrayWrapper.accessor inner = target.get(nouse);
	 
	  inner.setFlag(true);
     }
   }
 }
