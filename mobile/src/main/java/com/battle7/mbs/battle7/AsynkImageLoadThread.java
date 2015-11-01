package com.battle7.mbs.battle7;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AsynkImageLoadThread extends ContextSingletonBase<AsynkImageLoadThread> {
    private boolean bIsThreadActive;
    private LoadCallback mCallback;
    private HashMap<Integer, String> urlQueue;
    private HashMap<Integer, Bitmap> imageQueue;
    private Handler mHandler;

    public void init(Context context) {
        super.init(context);
        urlQueue = new HashMap<Integer, String>();
        mHandler = new Handler();
        imageQueue = new HashMap<Integer, Bitmap>();
    }

    public void startThread() {
        bIsThreadActive = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (bIsThreadActive) {
                    if(!urlQueue.isEmpty()) {
                        for (Map.Entry<Integer, String> e : urlQueue.entrySet()) {
                            Bitmap bmp = ApplicationHelper.downloadImage(e.getValue());
                            imageQueue.put(e.getKey(), bmp);
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                for (Map.Entry<Integer, Bitmap> e : imageQueue.entrySet()) {
                                    if (mCallback != null) mCallback.onLoad(e.getKey(), e.getValue());
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

    public void setImageQueue(int id,String url){
        urlQueue.put(id, url);
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
        public void onLoad(int id, Bitmap bitmap);
    }

    //デストラクタ
    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
