package com.battle7.mbs.battle7;

import android.app.Application;

public class Battle7Application extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		BluetoothServerThread.getInstance(BluetoothServerThread.class).init(this);
		BluetoothServerThread.getInstance(BluetoothServerThread.class).startServer();
		BluetoothClientThread.getInstance(BluetoothClientThread.class).init(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		BluetoothServerThread.getInstance(BluetoothServerThread.class).stopServer();
	}
}
