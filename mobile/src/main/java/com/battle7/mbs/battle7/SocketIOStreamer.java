package com.battle7.mbs.battle7;

import android.content.Context;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

//import io.socket.client.Socket;
//import io.socket.client.IO;
//import io.socket.emitter.Emitter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.google.gson.Gson;

public class SocketIOStreamer extends ContextSingletonBase<SocketIOStreamer> {
    private SocketIOEventCallback mCallback;
    private Socket mSocket;

    public void init(Context context) {
        super.init(context);
        try {
            mSocket = IO.socket(Config.SOCKET_SERVER_ROOT_URL);
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "connect!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "connect:" + o.toString());
                    }
                }
            });
            mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "error!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "error:" + o.toString());
                    }
                }
            });
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "timeout!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "timeout:" + o.toString());
                    }
                }
            });
            mSocket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "message!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "message:" + o.toString());
                        mCallback.onCall(o.toString());
                    }
                }
            });
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "discomment!!");
                    for (Object o : arg0) {
                        Log.d(Config.TAG, "discomment:" + o.toString());
                    }
                }
            });
            mSocket.on("tweetInfo", new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "tweetInfo");
                    Gson gson = new Gson();
                    for(Object o : arg0){
                        TwitterInfo twitterInfo = gson.fromJson(o.toString(), TwitterInfo.class);
                        Log.d(Config.TAG, "tweetInfo:" + o.toString());
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
      mSocket.connect();
    }

    public void emit(HashMap<String, Object> params){
        for(Map.Entry<String, Object> keyValue : params.entrySet()) {
            mSocket.emit(keyValue.getKey(), keyValue.getValue());
        }
        if(mCallback != null) mCallback.onEmit(params);
    }

    public void disConnect() {
        mSocket.disconnect();
    }

    public void release() {
        mCallback = null;
        disConnect();
    }

    public void setOnAudioRecordCallback(SocketIOEventCallback callback) {
        mCallback = callback;
    }

    public void removeOnAudioRecordCallback() {
        mCallback = null;
    }

    public interface SocketIOEventCallback{
        public void onCall(String receive);
        public void onEmit(HashMap<String, Object> emitted);
    }

    //デストラクタ
    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
