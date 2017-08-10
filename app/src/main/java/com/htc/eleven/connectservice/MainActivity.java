package com.htc.eleven.connectservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private EditText input = null;
    private Intent intent = null;
    private TextView tv = null;

    private MyService.ServiceBinder mBinder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.my_text);
        intent = new Intent(MainActivity.this, MyService.class);

        tv = (TextView) findViewById(R.id.tv);

        findViewById(R.id.btnStartService).setOnClickListener(this);
        findViewById(R.id.btnStopService).setOnClickListener(this);
        findViewById(R.id.btnBindService).setOnClickListener(this);
        findViewById(R.id.btnUnbindService).setOnClickListener(this);
        findViewById(R.id.btnSyncData).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartService:
                System.out.println("Start Button clicked !");
                intent.putExtra("data", input.getText().toString());
                startService(intent);
                break;
            case R.id.btnStopService:
                System.out.println("Stop Button clicked !");
                stopService(intent);
                break;
            case R.id.btnBindService:
                bindService(intent,this, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btnUnbindService:
                unbindService(this);
                break;
            case R.id.btnSyncData:
                mBinder.setData(input.getText().toString());
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mBinder = (MyService.ServiceBinder) iBinder;
        mBinder.getService().registerCallback(myCallback);
        mBinder.getService().registerCallback(printCallback);

        System.out.println("Service Connected !");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    private MyService.Callback printCallback = new MyService.Callback() {
        @Override
        public void onDataChanged(String data) {
            System.out.println("printCallback got message: " + data);
        }
    };

    private MyService.Callback myCallback = new MyService.Callback(){

        @Override
        public void onDataChanged(String data) {

            //tv.setText(data); onDataChanged was called from another thread, and Android forbidden other thread modify UI thread widget.

            // send a message to main thread to handle it.
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("data_callback", data);
            msg.setData(b);

            mHandler.sendMessage(msg);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            tv.setText(msg.getData().getString("data_callback"));
        }
    };
}
