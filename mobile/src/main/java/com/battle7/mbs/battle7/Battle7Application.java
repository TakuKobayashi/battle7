package com.battle7.mbs.battle7;

import android.app.Application;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class Battle7Application extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		SocketIOStreamer.getInstance(SocketIOStreamer.class).init(this);
		SocketIOStreamer.getInstance(SocketIOStreamer.class).connect();
		BluetoothClientThread.getInstance(BluetoothClientThread.class).init(this);
		BluetoothServerThread.getInstance(BluetoothServerThread.class).init(this);
		BluetoothServerThread.getInstance(BluetoothServerThread.class).startServer();
		AsynkImageLoadThread.getInstance(AsynkImageLoadThread.class).init(this);
		AsynkImageLoadThread.getInstance(AsynkImageLoadThread.class).startThread();
		GameLogicCalucurator.getInstance(GameLogicCalucurator.class).init(this);
		ExtraLayout.getInstance(ExtraLayout.class).init(this);
		ExtraLayout.getInstance(ExtraLayout.class).setBaseDisplaySize(1080,1920);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		BluetoothServerThread.getInstance(BluetoothServerThread.class).stopServer();
	}
}
