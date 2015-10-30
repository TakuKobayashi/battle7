package com.battle7.mbs.battle7;

import java.util.ArrayList;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	private ReceiveCallback mReceiveCallback;
	private ArrayList<BluetoothDevice> mDeviceList;

	public BluetoothBroadcastReceiver(){
		mDeviceList = new ArrayList<BluetoothDevice>();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		BluetoothDevice foundDevice;
		if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
			if(mReceiveCallback != null) mReceiveCallback.onDiscoveryStart();
		}
		if(BluetoothDevice.ACTION_FOUND.equals(action)){
			//デバイスが検出された
			foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mDeviceList.remove(foundDevice);
			mDeviceList.add(foundDevice);
			if(mReceiveCallback != null) mReceiveCallback.onDeviceFound(foundDevice);
			Log.d(Config.TAG, "found device:" + foundDevice.getName() + " address:" + foundDevice.getAddress());
		}
		if(BluetoothDevice.ACTION_NAME_CHANGED.equals(action)){
			//名前が検出された
			foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mDeviceList.remove(foundDevice);
			mDeviceList.add(foundDevice);
			if(mReceiveCallback != null) mReceiveCallback.onDeviceChanged(foundDevice);
			Log.d(Config.TAG, "changed device:" + foundDevice.getName() + " address:" + foundDevice.getAddress());
		}
		if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
			if(mReceiveCallback != null) mReceiveCallback.onDiscoverFinished(mDeviceList);
			Log.d(Config.TAG, "finish");
		}
	}

	public void setOnReceiveCallback(ReceiveCallback callback){
		mReceiveCallback = callback;
	}

	public interface ReceiveCallback{
		public void onDiscoveryStart();
		public void onDeviceFound(BluetoothDevice device);
		public void onDeviceChanged(BluetoothDevice device);
		public void onDiscoverFinished(ArrayList<BluetoothDevice> foundDevices);
	}
}
