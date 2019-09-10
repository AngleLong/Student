package com.angle.mediarecorder.handlerthread;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.appcompat.app.AppCompatActivity;

import com.angle.mediarecorder.R;

public class HandlerThreadActivity extends AppCompatActivity {

    private HandlerThread handlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_thread);

        initHandler();
    }

    private void initHandler() {
        //创建相应的线程
        handlerThread = new HandlerThread("Handler Thread");
        //启动线程
        handlerThread.start();
        //创建 一个Handler
        Handler tHandler = new Handler(handlerThread.getLooper());

        Thread t = Thread.currentThread();
        String name = t.getName();
        System.out.println("主name=" + name);

        tHandler.post(new Runnable() {
            @Override
            public void run() {
                Thread t = Thread.currentThread();
                String name = t.getName();
                System.out.println("name=" + name);
            }
        });
    }
}
