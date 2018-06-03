package beautifulworld.jp.utility.camera.eos7d;

import beautifulworld.jp.utility.camera.MessageReceiver;
import android.graphics.Bitmap;

public interface LiveViewMessageReceiver
 {
  public void LiveViewStarted();
  public void LiveViewExited();
  public void LiveViewImageRetrieved(Bitmap image);
  public void LiveViewImageSkipped();
  public void MovieModeStarted();
  public int getLiveViewMode();
 }
