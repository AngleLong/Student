package com.angle.mediarecorder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.angle.mediarecorder.camerautils.Camera21After;
import com.angle.mediarecorder.camerautils.Camera21Before;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainLibActivity extends AppCompatActivity {


    private static final String TAG = MainLibActivity.class.getSimpleName();
    private Camera21Before camera21Before;
    private AutoFitTextureView viewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lib);


//        mCamera21After = new Camera21After(MainLibActivity.this, viewById);


    }

    @Override
    protected void onResume() {
        super.onResume();

        RxPermissions mRxPermissions = new RxPermissions(MainLibActivity.this);
        Disposable sSubscribe = mRxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void accept(Boolean isSuccess) {
                        if (isSuccess) {
                            viewById = findViewById(R.id.showTextView);
                            camera21Before = new Camera21Before(MainLibActivity.this, viewById);
                            camera21Before.onResume();
                        }
                    }
                });
    }

    public void startVideo(View view) {
        camera21Before.startRecording(null);
    }

    public void endVideo(View view) {
        camera21Before.endRecording();
    }


    @Override
    protected void onPause() {
        super.onPause();
        camera21Before.onPause();
    }

    public void switchCamera(View view) {
        Log.e(TAG, "switchCamera: ");
        camera21Before.switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }
}
