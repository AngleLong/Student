package com.angle.mediarecorder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.angle.mediarecorder.camera.Camera21After;
import com.angle.mediarecorder.camera.Camera21Before;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class NewCameraActivity extends AppCompatActivity {

    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_camera);

        SurfaceView surfaceView = findViewById(R.id.surfaceView);

        mSurfaceHolder = surfaceView.getHolder();

        //设置监听
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                initCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCamera() {

        RxPermissions mRxPermissions = new RxPermissions(NewCameraActivity.this);
        Disposable sSubscribe = mRxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) {
                        if (isSuccess) {
                            Camera21Before camera21Before = new Camera21Before(NewCameraActivity.this);

                            camera21Before.getCameraCount();

                            camera21Before.openCamera(mSurfaceHolder);
                        }
                    }
                });


    }
}
