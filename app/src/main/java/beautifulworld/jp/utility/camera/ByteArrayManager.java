package beautifulworld.jp.utility.camera;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

public class ByteArrayManager
 {
  private static ByteArrayManager instance;	
	
  private static final int low = 512; //��e�ʂ̏���T�C�Y
  private static final int medium = 10240; //���e��
  private static final int large = 51200; //���e��
  private static final int huge = 4096000; //��e��400k

  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> lowsize;
  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> mediumsize;
  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> largesize;
  private  HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor> hugesize;
  

  private ByteArrayManager(){}//�O���N���X�̃C���X�^���X���֎~
  
  //ByteArrayManager��������
  public static void initialize()
   {
	if (instance != null) return;
	
	instance = new ByteArrayManager();
	instance.lowsize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
	instance.mediumsize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
	instance.largesize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
	instance.hugesize = new HashMap<ByteArrayWrapper, ByteArrayWrapper.accessor>();
   }
 
  //�o�C�g���Ԃ�
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

        if (inner.getFlag())//���g�p�̏ꍇ
         {
          inner.setFlag(false); //�g�p��ԂɕύX
          ByteArrayWrapper result = inner.getOuterInstance();
          result.setDataLength(length);
          return result;
         }
       }
    
    //���ׂĎg�p���̏ꍇ�͐V�K�ɐ�������B
      ByteArrayWrapper.accessor inner = ByteArrayWrapper.newInstance(allocatesize);
      ByteArrayWrapper result = inner.getOuterInstance();
      inner.setFlag(false);//�g�p��ԂɕύX
      result.setDataLength(length);//���ۂɎg�p����f�[�^�e�ʂ��w��(�K�{)
      
      target.put(result, inner);    

      return result;
     }
   } 

  
   //�o�C�g��������[�X����B
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
