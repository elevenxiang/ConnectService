package com.htc.eleven.connectservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Vector;

public class MyService extends Service {

    private String data = "default";
    private boolean runninng = false;
    public Vector<Callback> mCallback;

    public MyService() {
        mCallback = new Vector<Callback>();
    }

    public class ServiceBinder extends Binder {

        public void setData(String data){
            MyService.this.data = data;
            System.out.println("MyService Binder get the data !");
        }

        public MyService getService(){
            return MyService.this;
        }
    }

    public void registerCallback(Callback Callback) {
        this.mCallback.add(Callback);
    }

    @Override
    public IBinder onBind(Intent intent) {
       return new ServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("MyService onStartCommand !");
        data = intent.getStringExtra("data");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("MyService is running !");

        runninng = true;
        new  Thread(){
            @Override
            public void run() {
                super.run();

                int i = 0;
                while (runninng){

                    i++;
                    String tmp = i + ": " + data;
                    System.out.println(tmp);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(mCallback.size() > 0) {
                        for(Callback c : mCallback)
                            c.onDataChanged(tmp);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("MyService is stopped !");
        runninng = false;
    }

    public interface Callback {
        void onDataChanged(String data);
    }
}
