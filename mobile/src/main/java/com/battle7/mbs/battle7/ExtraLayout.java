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
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.Rect;

public class ExtraLayout extends ContextSingletonBase<ExtraLayout>{

  private static final float BASE_DISPLAY_WIDTH = 1080;
  private static final float BASE_DISPLAY_HEIGHT = 1920;
  private static final float BASE_ASPECT_RATIO = BASE_DISPLAY_WIDTH / BASE_DISPLAY_HEIGHT;

  public void init(Context context) {
    super.init(context);
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

    // 縦長の解像度端末
    if (BASE_ASPECT_RATIO > aspectRatio) {
      width = displaySize.width();
      height = (int)(width * BASE_DISPLAY_HEIGHT / BASE_DISPLAY_WIDTH);
    } else if (BASE_ASPECT_RATIO < aspectRatio) {
      height = displaySize.height();
      width = (int)(height * BASE_DISPLAY_WIDTH / BASE_DISPLAY_HEIGHT);
    } else {
      width = displaySize.width();
      height = displaySize.height();
    }

    return new Rect(0, 0, width, height);
  }

  public Rect getDisplayFullScreenResize() {
    Rect displaySize = getDisplaySize();
    float aspectRatio = ((float) displaySize.width()) / displaySize.height();
    int width = 0;
    int height = 0;

    // 縦長の解像度端末
    if (BASE_ASPECT_RATIO > aspectRatio) {
      height = displaySize.height();
      width = (int)(height * BASE_DISPLAY_WIDTH / BASE_DISPLAY_HEIGHT);
    } else if (BASE_ASPECT_RATIO < aspectRatio) {
      width = displaySize.width();
      height = (int)(width * BASE_DISPLAY_HEIGHT / BASE_DISPLAY_WIDTH);
    } else {
      width = displaySize.width();
      height = displaySize.height();
    }

    return new Rect(0, 0, width, height);
  }

  public float getResizeRatio() {
    //ipone版に合わせたサイズに計算する
    float sizeRatio = 0;
    Rect displaySize = getDisplaySize();
    float aspectRatio = ((float) displaySize.width() / displaySize.height());
    // 縦長の解像度端末
    if (BASE_ASPECT_RATIO >= aspectRatio) {
      sizeRatio = ((float)displaySize.width() / BASE_DISPLAY_WIDTH);
    } else {
      sizeRatio = ((float) displaySize.height() / BASE_DISPLAY_HEIGHT);
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