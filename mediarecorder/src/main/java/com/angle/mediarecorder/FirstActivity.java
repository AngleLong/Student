package com.angle.mediarecorder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.angle.mediarecorder.utils.BitmapUtils;
import com.vincent.videocompressor.VideoCompress;

/**
 * @author hejinlong
 * 用于
 */
public class FirstActivity extends AppCompatActivity {

    private static final int SOPENVIDEO = 0;
    private static final String TAG = FirstActivity.class.getSimpleName();
    private ImageView mShowIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mShowIv = findViewById(R.id.showIv);
    }

    public void open21Video(View view) {
        startActivityForResult(new Intent(this, CameraActivity.class), SOPENVIDEO);
    }


    public void compression(View view) {
        ///storage/emulated/0/Android/data/com.angle.student/files/1566456765302.mp4
        // 原始路径
        String desPath = getExternalFilesDir(null).getAbsolutePath() + "/" + "1566456712057.mp4";
        String Path = getExternalFilesDir(null).getAbsolutePath() + "/" + "压缩后的.mp4";

        VideoCompress.compressVideoMedium(desPath, Path, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "onSuccess: ");
            }

            @Override
            public void onFail() {
                Log.e(TAG, "onFail: ");
            }

            @Override
            public void onProgress(float percent) {
                Log.e(TAG, "onProgress: " + percent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(TAG, "onActivityResult: " + requestCode + "---" + resultCode);
        if (requestCode == SOPENVIDEO && resultCode == RESULT_OK && data != null) {
            Log.e(TAG, "onActivityResult: ");
            String url = data.getStringExtra("url");
            if (!TextUtils.isEmpty(url)) {
                Bitmap videoThumb = BitmapUtils.getVideoThumb(url);
                mShowIv.setImageBitmap(videoThumb);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
