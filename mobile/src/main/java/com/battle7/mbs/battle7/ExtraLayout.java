package com.battle7.mbs.battle7;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.Rect;

public class ExtraLayout extends ContextSingletonBase<ExtraLayout>{

  //端末の解像度に合わせて、画像が端末からはみ出ないようにアスペクト比を維持して調整する
  public static int DISPLAY_POLICY_SHOW_ALL = 0;
  //端末の解像度に合わせて、画像が端末からはみ出ても真ん中に表示されるようにアスペクト比を維持して調整する
  public static int DISPLAY_POLICY_EXACT_FIT = 1;
  //特に調整をかけない
  public static int DISPLAY_POLICY_NO_BORDER = 2;
  private int mDisplayPolicy = DISPLAY_POLICY_SHOW_ALL;
  private Rect mBaseDisplaySize;

  public void init(Context context) {
    super.init(context);
    mBaseDisplaySize = getDisplaySize();
  }

  public void setBaseDisplaySize(int width, int height){
    mBaseDisplaySize = new Rect(0,0,width, height);
  }

  public void setDisplayPolicy(int policy){
    mDisplayPolicy = policy;
  }

  public Point getDisplayMetrics(){
    WindowManager wm = (WindowManager) context.getSystemService( Context.WINDOW_SERVICE );
    Display display = wm.getDefaultDisplay();
    Point point = new Point();
    display.getSize(point);
    return point;
  }

  //端末の解像度を取得
  public Rect getDisplaySize(){
    Point point = getDisplayMetrics();
    return new Rect(0, 0, point.x, point.y);
  }

  public Rect getImageSize(Integer resId) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    options.inScaled = false;
    BitmapFactory.decodeResource(context.getResources(), resId, options);
    return new Rect(0, 0, options.outWidth, options.outHeight);
  }

  public Rect getImageResize(Integer resId) {
    Rect size = getImageSize(resId);
    //iphoneの解像度で使用しているしている画像をAndroidの解像度に合わせたサイズで表示させるための計算
    return new Rect(0,0, (int)((float)size.width() * getResizeRatio()), (int)((float)size.height() * getResizeRatio()));
  }

  public void setBaseImageView(ImageView imageView,Integer res){
    Rect imageSize = getImageResize(res);
    imageView.getLayoutParams().width = imageSize.width();
    imageView.getLayoutParams().height = imageSize.height();
    imageView.setImageResource(res);
  }

  public Rect getDisplayResize() {
    Rect displaySize = getDisplaySize();
    float aspectRatio = ((float) displaySize.width()) / displaySize.height();
    int width = 0;
    int height = 0;
    float baseAspectRatio = (float)((float)mBaseDisplaySize.width() / (float)mBaseDisplaySize.height());

    // 縦長の解像度端末
    if (baseAspectRatio > aspectRatio && (mDisplayPolicy == DISPLAY_POLICY_SHOW_ALL || mDisplayPolicy == DISPLAY_POLICY_EXACT_FIT)) {
      if (mDisplayPolicy == DISPLAY_POLICY_SHOW_ALL) {
        width = displaySize.width();
        height = (int) (width * (float)((float)mBaseDisplaySize.height() / (float)mBaseDisplaySize.width()));
      } else if (mDisplayPolicy == DISPLAY_POLICY_EXACT_FIT){
        height = displaySize.height();
        width = (int) (height * (float)((float)mBaseDisplaySize.width() / (float)mBaseDisplaySize.height()));
      }
    } else if (baseAspectRatio < aspectRatio && (mDisplayPolicy == DISPLAY_POLICY_SHOW_ALL || mDisplayPolicy == DISPLAY_POLICY_EXACT_FIT)) {
      if(mDisplayPolicy == DISPLAY_POLICY_SHOW_ALL) {
        height = displaySize.height();
        width = (int) (height * (float)((float)mBaseDisplaySize.width() / (float)mBaseDisplaySize.height()));
      } else if (mDisplayPolicy == DISPLAY_POLICY_EXACT_FIT){
        width = displaySize.width();
        height = (int) (width * (float)((float)mBaseDisplaySize.height() / (float)mBaseDisplaySize.width()));
      }
    } else {
      width = displaySize.width();
      height = displaySize.height();
    }

    return new Rect(0, 0, width, height);
  }

  public float getResizeRatio() {
    //ipone版に合わせたサイズに計算する
    float sizeRatio = 1;
    Rect displaySize = getDisplaySize();
    float aspectRatio = ((float) displaySize.width() / displaySize.height());
    float baseAspectRatio = (float)((float)mBaseDisplaySize.width() / (float)mBaseDisplaySize.height());
    // 縦長の解像度端末
    if (baseAspectRatio >= aspectRatio) {
      if (mDisplayPolicy == DISPLAY_POLICY_SHOW_ALL) {
        sizeRatio = ((float) displaySize.width() / (float)mBaseDisplaySize.width());
      }else{
        sizeRatio = ((float) displaySize.height() / (float)mBaseDisplaySize.height());
      }
    } else {
      if (mDisplayPolicy == DISPLAY_POLICY_SHOW_ALL) {
        sizeRatio = ((float) displaySize.height() / (float)mBaseDisplaySize.height());
      }else{
        sizeRatio = ((float) displaySize.width() / (float)mBaseDisplaySize.width());
      }
    }
    return sizeRatio;
  }

  public View getParenetView(Integer layoutID){
    //レイアウトを作って返す
    LinearLayout outSideLayout = new LinearLayout(context);
    outSideLayout.setGravity(Gravity.CENTER);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(layoutID, null);
    Rect disp = getDisplayResize();
    view.setLayoutParams(new LayoutParams(disp.width(),disp.height()));
    outSideLayout.addView(view);
    return outSideLayout;
  }

  public View getParenetViewWithBackgroundImage(Integer layoutID, ImageView image){
    //レイアウトを作って返す
    FrameLayout outSideLayout = new FrameLayout(context);
    Rect disp = getDisplayResize();
    outSideLayout.addView(image);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(layoutID, null);
    view.setLayoutParams(new FrameLayout.LayoutParams(disp.width(),disp.height(), Gravity.CENTER));
    outSideLayout.addView(view);
    return outSideLayout;
  }

  public OnTouchListener ImageTouchListener = new OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        ((ImageView) v).setColorFilter(new LightingColorFilter(Color.LTGRAY, 0));
        break;
      case MotionEvent.ACTION_CANCEL:
        ((ImageView) v).clearColorFilter();
        break;
      case MotionEvent.ACTION_UP:
        ((ImageView) v).clearColorFilter();
        break;
      case MotionEvent.ACTION_OUTSIDE:
        ((ImageView) v).clearColorFilter();
        break;
      }
      return false;
    }
  };

  public Bitmap resizeBaseBitmap(Bitmap image){
    float ratio = getResizeRatio();
    Bitmap resizedImage = Bitmap.createScaledBitmap(image, (int)(image.getWidth() * ratio),(int)(image.getHeight() * ratio), true);
    image.recycle();
    image = null;
    Bitmap result = resizedImage.copy(Bitmap.Config.ARGB_8888, true);
    resizedImage.recycle();
    resizedImage = null;
    return result;
  }
}