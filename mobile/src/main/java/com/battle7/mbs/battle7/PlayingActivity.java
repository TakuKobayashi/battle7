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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayingActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private String st = null;
    private RequestQueue mQueue;
    private TweetListAdapter tweetAdapter;
    private ProgressBar mCheerBar;
    private TextView scoreText;
    private static final long VIDEO_TIME = 112000;
    private int score = 0;
    private BluetoothClientThread mBlutoothClient;
    private static final int POINT = 10;
    private static final String TRIGER = "RIGE";
    private static final String ON = "ON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scoreText = (TextView) findViewById(R.id.scoreText);
        scoreText.setText(getString(R.string.score, score));

        tweetAdapter = new TweetListAdapter(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();

        mCheerBar = (ProgressBar) findViewById(R.id.cheerBar);
        mCheerBar.setMax(100);

        ((TextView) findViewById(R.id.reviveText)).setText(String.valueOf(mCheerBar.getProgress()));

        BluetoothServerThread.getInstance(BluetoothServerThread.class).setOnAudioRecordCallback(new BluetoothServerThread.ServerReceiveCallback() {
            @Override
            public void onTryConnection() {
                Log.d(Config.TAG, "server tryConnection");
            }

            @Override
            public void onConnectionSuccess() {
                Log.d(Config.TAG, "server Connection sucess");
            }

            @Override
            public void onReceive(int bytes, byte[] data) {
                try {
                    st = new String(data, "UTF-8");
                    Log.d(Config.TAG, "server bytes:" + bytes + " st:" + st);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d(Config.TAG, "server bytes:" + bytes + " error:" + e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationHelper.showToast(PlayingActivity.this, "st:" + st);
                    }
                });
            }

            @Override
            public void onSend(byte[] data) {
                String st = null;
                try {
                    st = new String(data, "UTF-8");
                    Log.d(Config.TAG, "server st:" + st);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d(Config.TAG, "server error:" + e.getMessage());
                }
            }
        });

        mBlutoothClient = BluetoothClientThread.getInstance(BluetoothClientThread.class);

        mBlutoothClient.setOnClientCallback(new BluetoothClientThread.ClientReceiveCallback() {
            @Override
            public void onTryConnection() {
                Log.d(Config.TAG, "client tryConnection");
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
                    if(st.contains(TRIGER) && st.contains(ON)){
                      Log.d(Config.TAG, "sucess:" + score);
                      score += POINT;
                      runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          scoreText.setText(getString(R.string.score, score));
                        }
                      });
                      HashMap<String, Object> params = new HashMap<String, Object>();
                      params.put("userId", UUID.randomUUID().toString());
                      params.put("timestamp", String.valueOf(System.currentTimeMillis()));
                      params.put("length", "1");
                      params.put("total", POINT);
                      httpRequest(Request.Method.POST, Config.ROOT_URL + "score?" + ApplicationHelper.makeUrlParams(params), null, new Response.Listener() {
                        @Override
                        public void onResponse(Object o) {
                        }
                      });
                    }
                    Log.d(Config.TAG,"st:" + st);
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
        mBlutoothClient.sendData("START1;".getBytes());

        ListView tweetTimelineList = (ListView) findViewById(R.id.tweetTimelineList);
        tweetTimelineList.setAdapter(tweetAdapter);

        mQueue = Volley.newRequestQueue(this);

        /*
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", UUID.randomUUID().toString());
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("length", "1");
        params.put("total", "3");

        httpRequest(Request.Method.POST, Config.ROOT_URL + "score?" + ApplicationHelper.makeUrlParams(params), null, new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                Log.d(Config.TAG, "sucess:" + o.toString());
            }
        });
        */
        SocketIOStreamer.getInstance(SocketIOStreamer.class).setOnAudioRecordCallback(new SocketIOStreamer.SocketIOEventCallback() {
            @Override
            public void onCall(String receive) {
                Gson gson = new Gson();
                TwitterInfo twitterInfo = gson.fromJson(receive, TwitterInfo.class);
                tweetAdapter.addTwitterInfo(twitterInfo);
                mCheerBar.setProgress(mCheerBar.getProgress() + 10);
                ((TextView) findViewById(R.id.reviveText)).setText(String.valueOf(mCheerBar.getProgress()));
            }

            @Override
            public void onEmit(HashMap<String, Object> emitted) {
                Log.d(Config.TAG, "emitted:" + emitted);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tweetAdapter.release();
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

    private void httpRequest(int method, String url , final Map<String, String> params, Response.Listener response){
        StringRequest request = new StringRequest(method ,url, response, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(Config.TAG, "error:" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };
        mQueue.add(request);
    }
}
