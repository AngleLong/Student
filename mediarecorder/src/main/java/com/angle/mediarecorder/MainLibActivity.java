package com.angle.mediarecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.view.View;

import com.angle.mediarecorder.camerautils.Camera21After;

public class MainLibActivity extends AppCompatActivity {


    private Camera21After mCamera21After;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lib);

        AutoFitTextureView viewById = findViewById(R.id.showTextView);


        mCamera21After = new Camera21After(MainLibActivity.this, viewById);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mRxPermissions = new RxPermissions(MainLibActivity.this);
//        Disposable sSubscribe = mRxPermissions.request(
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .subscribe(new Consumer<Boolean>() {
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                    @Override
//                    public void accept(Boolean isSuccess) {
//                        if (isSuccess) {
//
//                        }
//                    }
//                });



        mCamera21After.onResume();
    }

    public void startVideo(View view) {
        mCamera21After.startRecording(null);
    }

    public void endVideo(View view) {
        mCamera21After.endRecording();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mCamera21After.onPause();
    }

    public void switchCamera(View view){
        mCamera21After.switchCamera(CameraCharacteristics.LENS_FACING_FRONT);
    }
}
