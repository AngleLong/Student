package com.angle.mediarecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.angle.mediarecorder.camera.CameraUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author hejinlong
 * 用来展示相应的视频录制功能
 * <p>
 * 参考的文章:
 * https://www.imooc.com/article/253961
 * https://blog.csdn.net/u010126792/article/details/86650116
 * <p>
 * 期间遇到的问题
 * Camera的兼容性
 */
public class MediaActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = MediaActivity.class.getSimpleName();
    private static int mOrientation = 0;

    private MediaRecorder mMediaRecorder;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private boolean havePermission;
    private Camera mCamera;
    private static int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private RxPermissions mRxPermissions;

    String srcPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/mediarecorder/";
    String srcName = "video.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        mRxPermissions = new RxPermissions(MediaActivity.this);
        Disposable sSubscribe = mRxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) {
                        if (isSuccess) {
                            havePermission = true;
                            initSurfaceView();
                        } else {
                            havePermission = false;
                        }
                    }
                });
    }

    private void initSurfaceView() {
        if (mSurfaceView == null) {
            mSurfaceView = findViewById(R.id.surfaceView);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
            //这个添加之后就能看到相应的预览效果
            mSurfaceView.setFocusable(true);
            mSurfaceView.setFocusableInTouchMode(true);
            mSurfaceView.setKeepScreenOn(true);
        }
    }

    /**
     * 开始录制视频
     *
     * @param view 展示的View
     */
    public void startVideo(View view) {

        //创建MediaRecorder对象
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();

        mCamera.unlock();


        Log.e(TAG, "startVideo: " + srcPath + srcName);
        //创建文件夹
        File mRecorderFile = new File(srcPath + srcName);
        try {
            if (!mRecorderFile.getParentFile().exists()) {
                mRecorderFile.getParentFile().mkdirs();
                mRecorderFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOrientationHint(mOrientation);

        //从麦克风采集
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfile mCamcorderProfile = CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_BACK, CamcorderProfile.QUALITY_HIGH);
        System.out.println("============mCamcorderProfile============" + mCamcorderProfile.videoFrameWidth + "   " + mCamcorderProfile.videoFrameHeight);
        mMediaRecorder.setProfile(mCamcorderProfile);
        //使用CamcorderProfile做配置的话，输出格式，音频编码，视频编码 不要写,否则会报错（崩溃）
        /*mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);*/

        //设置录制视频的大小，其实Camera也必须要和这个比例相同，此处为了简单不做处理
        mMediaRecorder.setVideoSize(mCamcorderProfile.videoFrameWidth, mCamcorderProfile.videoFrameHeight);
        //提高帧频率，录像模糊，花屏，绿屏可写上调试
//        mMediaRecorder.setVideoEncodingBitRate(mCamcorderProfile.videoFrameWidth*mCamcorderProfile.videoFrameHeight*24*16);
        mMediaRecorder.setVideoFrameRate(24);
        //所有android系统都支持的适中采样的频率
        mMediaRecorder.setAudioSamplingRate(44100);
        //设置文件录音的位置
        mMediaRecorder.setOutputFile(mRecorderFile.getAbsolutePath());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        //开始录音
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "surfaceCreated: ");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG, "surfaceChanged: ");
        if (havePermission) {
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//
//                }
//            }.start();
            initCamera();
        }

    }

    /**
     * 初始化相应的Camera
     */
    private void initCamera() {
        //打印摄像头的信息
        CameraUtils.getCameraCount();

        if (mCamera != null) {
//            releaseCamera();
            System.out.println("===================releaseCamera=============");
        }

        //打开摄像头
        mCamera = Camera.open(mCameraID);
        System.out.println("===================openCamera=============");

        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRecordingHint(true);

            //设置获取数据
            parameters.setPreviewFormat(ImageFormat.NV21);
            //parameters.setPreviewFormat(ImageFormat.YUV_420_888);

            //通过setPreviewCallback方法监听预览的回调：
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    //这里面的Bytes的数据就是NV21格式的数据,或者YUV_420_888的数据


                }
            });

            if (mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            mCamera.setParameters(parameters);

            calculateCameraPreviewOrientation(this);
//            Camera.Size tempSize = setPreviewSize(mCamera, useHeight, useWidth);
//            {
//                //此处可以处理，获取到tempSize，如果tempSize和设置的SurfaceView的宽高冲突，重新设置SurfaceView的宽高
//            }
//
//            setPictureSize(mCamera, useHeight, useWidth);
            mCamera.setDisplayOrientation(mOrientation);
            int degree = calculateCameraPreviewOrientation(this);
            mCamera.setDisplayOrientation(degree);
            mCamera.startPreview();
        }
    }

    private Camera.Size setPreviewSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        Point point = new Point(expectWidth, expectHeight);
        Camera.Size size = findProperSize(point, parameters.getSupportedPictureSizes());
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);
        return size;
    }


    /**
     * 找出最合适的尺寸，规则如下：
     * 1.将尺寸按比例分组，找出比例最接近屏幕比例的尺寸组
     * 2.在比例最接近的尺寸组中找出最接近屏幕尺寸且大于屏幕尺寸的尺寸
     * 3.如果没有找到，则忽略2中第二个条件再找一遍，应该是最合适的尺寸了
     */
    private static Camera.Size findProperSize(Point surfaceSize, List<Camera.Size> sizeList) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || sizeList == null) {
            return null;
        }

        int surfaceWidth = surfaceSize.x;
        int surfaceHeight = surfaceSize.y;

        List<List<Camera.Size>> ratioListList = new ArrayList<>();
        for (Camera.Size size : sizeList) {
            addRatioList(ratioListList, size);
        }

        final float surfaceRatio = (float) surfaceWidth / surfaceHeight;
        List<Camera.Size> bestRatioList = null;
        float ratioDiff = Float.MAX_VALUE;
        for (List<Camera.Size> ratioList : ratioListList) {
            float ratio = (float) ratioList.get(0).width / ratioList.get(0).height;
            float newRatioDiff = Math.abs(ratio - surfaceRatio);
            if (newRatioDiff < ratioDiff) {
                bestRatioList = ratioList;
                ratioDiff = newRatioDiff;
            }
        }

        Camera.Size bestSize = null;
        int diff = Integer.MAX_VALUE;
        assert bestRatioList != null;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (size.height >= surfaceHeight && newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        if (bestSize != null) {
            return bestSize;
        }

        diff = Integer.MAX_VALUE;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        return bestSize;
    }

    private static void addRatioList(List<List<Camera.Size>> ratioListList, Camera.Size size) {
        float ratio = (float) size.width / size.height;
        for (List<Camera.Size> ratioList : ratioListList) {
            float mine = (float) ratioList.get(0).width / ratioList.get(0).height;
            if (ratio == mine) {
                ratioList.add(size);
                return;
            }
        }

        List<Camera.Size> ratioList = new ArrayList<>();
        ratioList.add(size);
        ratioListList.add(ratioList);
    }

    /**
     * 设置预览角度，setDisplayOrientation本身只能改变预览的角度
     * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的
     * 拍摄的照片需要自行处理
     * 这里Nexus5X的相机简直没法吐槽，后置摄像头倒置了，切换摄像头之后就出现问题了。
     *
     * @param activity
     */
    public static int calculateCameraPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mOrientation = result;
        System.out.println("=========orienttaion=============" + result);
        return result;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
    }

    public void endVideo(View view) {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mCamera.lock();
        }
    }
}
