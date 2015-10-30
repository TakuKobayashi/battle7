package com.battle7.mbs.battle7;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothServerThread extends ContextSingletonBase<BluetoothServerThread> {
    private boolean bIsActive;
    private ServerReceiveCallback mCallback;
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mSocket;

    public void init(Context context) {
        super.init(context);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            mServerSocket = adapter.listenUsingRfcommWithServiceRecord("sample", UUID.randomUUID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecording() {
        bIsActive = true;
        // 録音スレッド
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (bIsActive) {
                    try {
                        // This is a blocking call and will only return on a
                        // successful connection or an exception
                        mSocket = mServerSocket.accept();
                    } catch (IOException e) {
                        Log.e(Config.TAG, "accept() failed", e);
                        break;
                    }

                    // If a connection was accepted
                    if (mSocket != null) {
                        /*
                        synchronized (BluetoothChatService.this) {
                            switch (mState) {
                                case STATE_LISTEN:
                                case STATE_CONNECTING:
                                    // Situation normal. Start the connected thread.
                                    connected(socket, socket.getRemoteDevice());
                                    break;
                                case STATE_NONE:
                                case STATE_CONNECTED:
                                    // Either not ready or already connected. Terminate new socket.
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        Log.e(TAG, "Could not close unwanted socket", e);
                                    }
                                    break;
                            }
                        }
                        */
                    }
                }
            }
        });
        thread.start();
    }

    public void stopRecording() {
        bIsActive = false;
        if(mServerSocket != null){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void release() {
        mCallback = null;
        stopRecording();
    }

    public void setOnAudioRecordCallback(ServerReceiveCallback callback) {
        mCallback = callback;
    }

    public void removeOnAudioRecordCallback() {
        mCallback = null;
    }

    public interface ServerReceiveCallback{
        public void onReceive(byte[] raw);
    }

    //デストラクタ
    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
