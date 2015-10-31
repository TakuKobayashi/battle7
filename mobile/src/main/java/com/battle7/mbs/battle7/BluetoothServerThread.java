package com.battle7.mbs.battle7;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothServerThread extends ContextSingletonBase<BluetoothServerThread> {
    private boolean bIsServerActive;
    private boolean bIsConnectionActive;
    private ServerReceiveCallback mCallback;
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mSocket;
    private final UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream mSocketIS = null;
    private OutputStream mSocketOS = null;

    public void init(Context context) {
        super.init(context);
        setupBluetoothServer();
    }

    private void setupBluetoothServer(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            mServerSocket = adapter.listenUsingRfcommWithServiceRecord(adapter.getName(), sppUuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        bIsServerActive = true;
        // 録音スレッド
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (bIsServerActive) {
                    Log.d(Config.TAG, "startServer");
                    try {
                        // This is a blocking call and will only return on a
                        // successful connection or an exception
                        mSocket = mServerSocket.accept();
                    } catch (IOException e) {
                        Log.e(Config.TAG, "accept() failed", e);
                    }
                    if(mCallback != null) mCallback.onTryConnection();
                    Log.d(Config.TAG, "socket:" + mSocket);

                    // If a connection was accepted
                    if (mSocket != null) {
                        Log.d(Config.TAG, "connect");
                        socketConnection();
                        try {
                            //処理が完了したソケットは閉じる。
                            mServerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            setupBluetoothServer();
                        }
                        bIsServerActive = false;
                    }
                }
            }
        });
        thread.start();
    }

    private void socketConnection(){
        bIsConnectionActive = true;
        try {
            mSocketIS = mSocket.getInputStream();
            mSocketOS = mSocket.getOutputStream();
            if(mCallback != null) mCallback.onConnectionSuccess();
        } catch (IOException e) {
            Log.e(Config.TAG, "temp sockets not created", e);
        }

        // 録音スレッド
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (bIsConnectionActive) {
                    byte[] buffer = new byte[1024];
                    int bytes = 0;
                    try {
                        // Read from the InputStream
                        bytes = mSocketIS.read(buffer);
                    } catch (IOException e) {
                        Log.d(Config.TAG, "disconnected:" + e.getMessage());
                    }
                    Log.d(Config.TAG, "bytes:" + bytes);
                    if(bytes > 0) {
                        if (mCallback != null) mCallback.onReceive(bytes, buffer);
                    }
                }
            }
        });
        thread.start();
    }

    public void sendData(byte[] buffer) {
        if(mSocketOS == null) return;
        try {
            mSocketOS.write(buffer);
            if(mCallback != null) mCallback.onSend(buffer);
        } catch (IOException e) {
            Log.d(Config.TAG, "Exception during write" + e.getMessage());
        }
    }

    public void stopServer() {
        bIsServerActive = false;
        bIsConnectionActive = false;
        if(mServerSocket != null){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(Config.TAG, "Exception server close" + e.getMessage());
            }
        }
        if(mSocket != null){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(Config.TAG, "Exception socket close" + e.getMessage());
            }
        }
        Log.d(Config.TAG, "serverStopped");
    }

    public void release() {
        mCallback = null;
        stopServer();
    }

    public void setOnAudioRecordCallback(ServerReceiveCallback callback) {
        mCallback = callback;
    }

    public void removeOnAudioRecordCallback() {
        mCallback = null;
    }

    public interface ServerReceiveCallback{
        public void onTryConnection();
        public void onConnectionSuccess();
        public void onReceive(int bytes, byte[] data);
        public void onSend(byte[] data);
    }

    //デストラクタ
    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
