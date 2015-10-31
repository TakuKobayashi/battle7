package com.battle7.mbs.battle7;

import android.view.Display;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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

public class ExtraLayout {

  private static final float BASE_DISPLAY_WIDTH = 1080;
  private static final float BASE_DISPLAY_HEIGHT = 1920;
  private static final float BASE_ASPECT_RATIO = BASE_DISPLAY_WIDTH / BASE_DISPLAY_HEIGHT;

  private static Point getDisplayMetrics(Activity activity){
    Display display = activity.getWindowManager().getDefaultDisplay();
    Point point = new Point();
    display.getSize(point);
    return point;
  }

  //端末の解像度を取得
  public static Rect getDisplaySize(Activity activity){
    Point point = getDisplayMetrics(activity);
    return new Rect(0, 0, point.x, point.y);
  }

  public static Rect getImageSize(Context context, Integer resId) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    options.inScaled = false;
    BitmapFactory.decodeResource(context.getResources(), resId, options);
    return new Rect(0, 0, options.outWidth, options.outHeight);
  }

  public static Rect getImageResize(Activity activity, Integer resId) {
    Rect size = ExtraLayout.getImageSize(activity, resId);
    //iphoneの解像度で使用しているしている画像をAndroidの解像度に合わせたサイズで表示させるための計算
    return new Rect(0,0, (int)((float)size.width() * getResizeRatio(activity)), (int)((float)size.height() * getResizeRatio(activity)));
  }

  public static void setBaseImageView(Activity act,ImageView imageView,Integer res){
    Rect imageSize = ExtraLayout.getImageResize(act, res);
    imageView.getLayoutParams().width = imageSize.width();
    imageView.getLayoutParams().height = imageSize.height();
    imageView.setImageResource(res);
  }

  public static Rect getDisplayResize(Activity activity) {
    Rect displaySize = getDisplaySize(activity);
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

  public static Rect getDisplayFullScreenResize(Activity activity) {
    Rect displaySize = getDisplaySize(activity);
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

  public static float getResizeRatio(Activity activity) {
    //ipone版に合わせたサイズに計算する
    float sizeRatio = 0;
    Rect displaySize = getDisplaySize(activity);
    float aspectRatio = ((float) displaySize.width() / displaySize.height());
    // 縦長の解像度端末
    if (BASE_ASPECT_RATIO >= aspectRatio) {
      sizeRatio = ((float)displaySize.width() / BASE_DISPLAY_WIDTH);
    } else {
      sizeRatio = ((float) displaySize.height() / BASE_DISPLAY_HEIGHT);
    }
    return sizeRatio;
  }

  public static View getParenetView(Activity act,Integer layoutID){
    //レイアウトを作って返す
    LinearLayout outSideLayout = new LinearLayout(act);
    outSideLayout.setGravity(Gravity.CENTER);
    View view = act.getLayoutInflater().inflate(layoutID, null);
    Rect disp = getDisplayResize(act);
    view.setLayoutParams(new LayoutParams(disp.width(),disp.height()));
    outSideLayout.addView(view);
    return outSideLayout;
  }

  public static View getParenetViewWithBackgroundImage(Activity act,Integer layoutID, ImageView image){
    //レイアウトを作って返す
    FrameLayout outSideLayout = new FrameLayout(act);
    Rect disp = getDisplayResize(act);
    outSideLayout.addView(image);
    View view = act.getLayoutInflater().inflate(layoutID, null);
    view.setLayoutParams(new FrameLayout.LayoutParams(disp.width(),disp.height(), Gravity.CENTER));
    outSideLayout.addView(view);
    return outSideLayout;
  }

  public static OnTouchListener ImageTouchListener = new OnTouchListener() {
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

  public static Bitmap resizeBaseBitmap(Activity activity, Bitmap image){
    float ratio = getResizeRatio(activity);
    Bitmap resizedImage = Bitmap.createScaledBitmap(image, (int)(image.getWidth() * ratio),(int)(image.getHeight() * ratio), true);
    image.recycle();
    image = null;
    Bitmap result = resizedImage.copy(Bitmap.Config.ARGB_8888, true);
    resizedImage.recycle();
    resizedImage = null;
    return result;
  }
}