package com.battle7.mbs.battle7;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> deviceList;
    private ArrayAdapter mDeviceAdapter;
    private BluetoothBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_view);

        deviceList = new ArrayList<BluetoothDevice>();
        mDeviceAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        mReceiver = new BluetoothBroadcastReceiver();
        mReceiver.setOnReceiveCallback(new BluetoothBroadcastReceiver.ReceiveCallback() {
            @Override
            public void onDiscoveryStart() {
                Log.d(Config.TAG, "scanStart");
            }

            @Override
            public void onDiscoverFinished(ArrayList<BluetoothDevice> foundDevices) {
                showDeviceList(foundDevices);
                Log.d(Config.TAG, "scanFinish");
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                ArrayList list = new ArrayList();
                list.add(device);
                showDeviceList(list);
                Log.d(Config.TAG, "found");
            }

            @Override
            public void onDeviceChanged(BluetoothDevice device) {
                ArrayList list = new ArrayList();
                list.add(device);
                showDeviceList(list);
                Log.d(Config.TAG, "changed");
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        showDeviceList(mBluetoothAdapter.getBondedDevices());
        Button serach = (Button) findViewById(R.id.searchDevicebutton);
        serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isDiscovering()) {
                    //検索中の場合は検出をキャンセルする
                    mBluetoothAdapter.cancelDiscovery();
                }
                //デバイスを検索する
                //一定時間の間検出を行う
                mBluetoothAdapter.startDiscovery();
            }
        });

        ListView deviceListView = (ListView) findViewById(R.id.deviceList);
        deviceListView.setAdapter(mDeviceAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
                Log.d(Config.TAG, deviceList.get(position).getName() + " : " + deviceList.get(position).getAddress());
            }
        });

        Button scan = (Button) findViewById(R.id.scanDeviceButton);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        });
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            scan.setVisibility(View.VISIBLE);
        }else{
            scan.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter.isDiscovering()) {
            //検索中の場合は検出をキャンセルする
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mReceiver);
    }

    private void showDeviceList(Collection<BluetoothDevice> devices){
        deviceList.removeAll(devices);
        deviceList.addAll(devices);
        mDeviceAdapter.clear();
        for(BluetoothDevice device : deviceList){
            mDeviceAdapter.add(device.getName() + " : " + device.getAddress());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
