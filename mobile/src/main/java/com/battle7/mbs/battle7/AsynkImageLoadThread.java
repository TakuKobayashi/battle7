package com.battle7.mbs.battle7;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class AsynkImageLoadThread extends ContextSingletonBase<AsynkImageLoadThread> {
    private boolean bIsThreadActive;
    private LoadCallback mCallback;
    private ArrayList<String> urlQueue;
    private ArrayList<Bitmap> imageQueue;
    private Handler mHandler;

    public void init(Context context) {
        super.init(context);
        urlQueue = new ArrayList<String>();
        mHandler = new Handler();
        imageQueue = new ArrayList<Bitmap>();
    }

    public void startThread() {
        bIsThreadActive = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (bIsThreadActive) {
                    if(!urlQueue.isEmpty()) {
                        for (int i = 0; i < urlQueue.size(); ++i) {
                            Bitmap bmp = ApplicationHelper.downloadImage(urlQueue.get(i));
                            imageQueue.add(bmp);
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0;i < imageQueue.size();++i) {
                                    if (mCallback != null) mCallback.onLoad(imageQueue.get(i));
                                }
                                imageQueue.clear();
                            }
                        });
                        urlQueue.clear();
                    }
                }
            }
        });
        thread.start();
    }

    public void setImageQueue(String url){
        urlQueue.add(url);
    }

    public void stopServer() {
        bIsThreadActive = false;
    }

    public void release() {
        mCallback = null;
        urlQueue.clear();
        imageQueue.clear();
    }

    public void setOnAudioRecordCallback(LoadCallback callback) {
        mCallback = callback;
    }

    public void removeOnAudioRecordCallback() {
        mCallback = null;
    }

    public interface LoadCallback{
        public void onLoad(Bitmap bitmap);
    }

    //デストラクタ
    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
