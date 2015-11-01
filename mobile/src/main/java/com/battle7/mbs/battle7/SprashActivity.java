package com.battle7.mbs.battle7;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import java.util.UUID;

public class SprashActivity extends AppCompatActivity {
    private static final int START_SCREEN_DISPLAY_TIME = 1000; // Millisecond

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ExtraLayout.getParenetView(this, R.layout.sprash_view));

        ImageView image = (ImageView) findViewById(R.id.sprashImage);
        image.setImageResource(R.mipmap.gundom_bg);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
        SharedPreferences sp = Preferences.getCommonPreferences(this);
        if(sp.getString("userId", null) == null) {
            Preferences.saveCommonParam(this, "userId", UUID.randomUUID().toString());
        }
        scesuleNextActivity();
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void scesuleNextActivity(){
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //次のactivityを実行
                Intent intent = new Intent(SprashActivity.this, BluetoothSettingActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });
        handler.sendEmptyMessageDelayed(0, START_SCREEN_DISPLAY_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationHelper.releaseImageView((ImageView) findViewById(R.id.sprashImage));
    }
}
