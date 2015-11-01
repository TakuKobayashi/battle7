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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BluetoothSettingActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> deviceList;
    private ArrayAdapter mDeviceAdapter;
    private BluetoothBroadcastReceiver mReceiver;
    private String st = null;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_setting_view);

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
        Button serach = (Button) findViewById(R.id.searchDevicebutton2);
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

        BluetoothClientThread.getInstance(BluetoothClientThread.class).setOnClientCallback(new BluetoothClientThread.ClientReceiveCallback() {
            @Override
            public void onTryConnection() {
                Log.d(Config.TAG, "client tryConnection");
                Intent intent = new Intent(BluetoothSettingActivity.this, PlayingActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onConnectionSuccess() {
                Log.d(Config.TAG, "client connection success");
            }

            @Override
            public void onReceive(int bytes, byte[] data) {
                String st = null;
                try {
                    st = new String(data, "UTF-8");
                    Log.d(Config.TAG, "client bytes:" + bytes + " st:" + st);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d(Config.TAG, "client bytes:" + bytes + " error:" + e.getMessage());
                }
            }

            @Override
            public void onSend(byte[] data) {
                String st = null;
                try {
                    st = new String(data, "UTF-8");
                    Log.d(Config.TAG, "st:" + st);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d(Config.TAG, "error:" + e.getMessage());
                }
            }
        });

        ListView deviceListView = (ListView) findViewById(R.id.deviceList2);
        deviceListView.setAdapter(mDeviceAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
                Log.d(Config.TAG, deviceList.get(position).getName() + " : " + deviceList.get(position).getAddress());
                if (mBluetoothAdapter.isDiscovering()) {
                    //検索中の場合は検出をキャンセルする
                    mBluetoothAdapter.cancelDiscovery();
                }
                BluetoothClientThread.getInstance(BluetoothClientThread.class).startConnection(deviceList.get(position));
            }
        });

        Button scan = (Button) findViewById(R.id.scanDeviceButton2);
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
