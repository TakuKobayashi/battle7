package com.battle7.mbs.battle7;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothClientThread extends ContextSingletonBase<BluetoothClientThread> {
    private boolean bIsConnectionActive;
    private ClientReceiveCallback mCallback;
    private BluetoothSocket mSocket;
    private final UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream mSocketIS = null;
    private OutputStream mSocketOS = null;

    public void init(Context context) {
        super.init(context);
    }

    public void startConnection(BluetoothDevice device){
        try {
            mSocket = device.createRfcommSocketToServiceRecord(sppUuid);
        } catch (IOException e) {
            Log.e(Config.TAG, "create() failed", e);
        }
        Log.e(Config.TAG, "create:" + mSocket);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
              try {
                mSocket.connect();
              } catch (IOException e) {
                // Close the socket
                try {
                  mSocket.close();
                } catch (IOException e2) {
                   Log.e(Config.TAG, "unable to close() socket during connection failure", e2);
                }
              }
              if(mCallback != null) mCallback.onTryConnection();
              Log.d(Config.TAG, "socket:" + mSocket);
              socketConnection();
            }
        });
        thread.start();
    }

    private void socketConnection(){
        bIsConnectionActive = true;
        try {
            mSocketIS = mSocket.getInputStream();
            mSocketOS = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(Config.TAG, "client temp sockets not created", e);
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
                        Log.d(Config.TAG, "client disconnected:" + e.getMessage());
                    }
                    Log.d(Config.TAG, "client bytes:" + bytes);
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

    public void stopClient() {
        bIsConnectionActive = false;
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
        stopClient();
    }

    public void setOnClientCallback(ClientReceiveCallback callback) {
        mCallback = callback;
    }

    public void removeOnClientCallback() {
        mCallback = null;
    }

    public interface ClientReceiveCallback{
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
