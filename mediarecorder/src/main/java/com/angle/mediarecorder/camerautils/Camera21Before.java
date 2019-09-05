package com.angle.mediarecorder.camerautils;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.angle.mediarecorder.AutoFitTextureView;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 21版本之前的摄像头使用用类(Camera)
 * https://blog.csdn.net/u010126792/article/details/86650116
 * https://blog.csdn.net/xx326664162/article/details/53350551
 */
public class Camera21Before implements CameraImpl {

    private Activity mActivity;
    private AutoFitTextureView mTextureView;
    private SurfaceTexture mSurface;
    private Camera.Size mVideoSize;

    private String mNextVideoAbsolutePath;

    //手机旋转对应的调整角度
    /**
     * 传感器正常方向
     */
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    /**
     * 传感器反方向
     */
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    /**
     * 设置一个数组进行保存，用来重置方向
     */
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        /*
         * 关于TextureView可以参考这篇文章：https://blog.csdn.net/hardworkingant/article/details/72784044
         * 因为TextureView只支持硬件加速，关于硬件加速请看这篇文章：https://www.jianshu.com/p/9cd7097a4fcf
         */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            //在SurfaceTexture准备使用时调用
            mSurface = surfaceTexture;
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            //当SurfaceTexture缓冲区大小更改时调用。
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            // 当指定SurfaceTexture即将被销毁时调用。如果返回true，则调用此方法后，表面纹理中不会发生渲染。
            // 如果返回false，则客户端需要调用release()。大多数应用程序应该返回true。
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            //：当指定SurfaceTexture的更新时调用updateTexImage()。
        }

    };
    private String mCameraId;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private int mSensorOrientation;
    private Camera.Parameters parameters;

    public Camera21Before(Activity activity, AutoFitTextureView textureView) {
        mActivity = activity;

        mTextureView = textureView;
        //这个添加之后就能看到相应的预览效果
        mTextureView.setFocusable(true);
        mTextureView.setFocusableInTouchMode(true);
        mTextureView.setKeepScreenOn(true);
    }

    @Override
    public void onResume() {
        //检查Texture是否可用
        if (mTextureView.isAvailable()) {
            //如果可用的话直接打开Camera
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            //设置相应的监听
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should not to be called until the mCamera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mVideoSize.width, mVideoSize.height);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mVideoSize.height,
                    (float) viewWidth / mVideoSize.width);
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    @Override
    public boolean openCamera(int cameraOrientation, int width, int height) {
        if (Camera.CameraInfo.CAMERA_FACING_BACK == cameraOrientation) {
            mCameraId = openBackCamera();
        } else {
            mCameraId = openFrontCamera();
        }

        try {
            //开启相机
            mCamera = Camera.open(Integer.valueOf(mCameraId));
            //相机参数(通过传入CameraInfo)
            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraOrientation, cameraInfo);
            mSensorOrientation = cameraInfo.orientation;
            Log.e(TAG, "相机屏幕方向" + mSensorOrientation);

            //相机服务设置
            if (parameters == null) {
                parameters = mCamera.getParameters();

                //获取屏幕尺寸
                mVideoSize = chooseVideoSize(parameters.getSupportedPreviewSizes());

                //告诉相机是录制视频
                parameters.setRecordingHint(true);

                //自动对焦
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                //设置预览尺寸
                parameters.setPreviewSize(mVideoSize.width, mVideoSize.height);

                //设置相机服务设置
                mCamera.setParameters(parameters);
            }

            //设置相机方向
            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            Log.e(TAG, "屏幕方向为：" + rotation);
            mCamera.setDisplayOrientation(DEFAULT_ORIENTATIONS.get(rotation));

            //开启预览
            openPreview();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "相机配置失败原因为：" + e.toString());
            return false;
        }
    }

    private static Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }

        return optimalSize;
    }

    /**
     * 设置视频的尺寸,这里处理的是4:3的视频并且视频不能超过1080p否则MediaRecorder处理不了
     *
     * @param choices 可用尺寸列表
     * @return 视频的尺寸
     */
    private static Camera.Size chooseVideoSize(List<Camera.Size> choices) {
        for (Camera.Size size : choices) {
            if (size.width == size.height * 4 / 3 && size.width <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices.get(choices.size() - 1);
    }

    @Override
    public void closeCamera() {
        try {
            closePreview();
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        }
    }

    @Override
    public boolean openPreview() {
        try {
            //设置实时预览
            if (mSurface == null) {
                mSurface = mTextureView.getSurfaceTexture();
            }
            mCamera.setPreviewTexture(mSurface);
            //开启预览
            mCamera.startPreview();

            //创建相应的MediaRecorder
            mMediaRecorder = new MediaRecorder();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void closePreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean initMediaRecorder() {
        try {
            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
            //设置音频来源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置视频来源
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            //设置输出格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 设置视频的编码格式
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            // 设置音频的编码格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            // 设置输出
            mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
            //设置路径
            if (TextUtils.isEmpty(mNextVideoAbsolutePath)) {
                mNextVideoAbsolutePath = getVideoFilePath(mActivity);
            }
            mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
            //设置帧率
            mMediaRecorder.setVideoFrameRate(60);

            //设置尺寸
            Log.e(TAG, "initMediaRecorder: " + mVideoSize.width + "------------" + mVideoSize.height);
            mMediaRecorder.setVideoSize(mVideoSize.width, mVideoSize.height);

            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            Log.e(TAG, "setUpMediaRecorder:===> " + rotation);
            switch (mSensorOrientation) {
                case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                    Log.e(TAG, "setUpMediaRecorder: 1" + mSensorOrientation);
                    mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                    break;
                case SENSOR_ORIENTATION_INVERSE_DEGREES:
                    Log.e(TAG, "setUpMediaRecorder: 2" + mSensorOrientation);
                    mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                    break;
                default:
            }
            //准备
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + System.currentTimeMillis() + ".mp4";
    }


    @Override
    public boolean startRecording(String path) {
        if (!mTextureView.isAvailable()) {
            return false;
        }

        if (TextUtils.isEmpty(path)) {
            mNextVideoAbsolutePath = getVideoFilePath(mActivity);
        } else {
            mNextVideoAbsolutePath = path;
        }

        try {
//            closePreview();
            initMediaRecorder();

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(mVideoSize.width, mVideoSize.height);

            mMediaRecorder.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void endRecording() {
        // UI
        // Stop recording
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }

        Toast.makeText(mActivity, "Video saved: " + mNextVideoAbsolutePath,
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Video saved: " + mNextVideoAbsolutePath);
        mNextVideoAbsolutePath = null;
        openPreview();
    }

    @Override
    public void switchCamera(int cameraFacing) {
//        closePreview();
//        openCamera(cameraFacing, mTextureView.getWidth(), mTextureView.getHeight());
        if (mCamera != null) {
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
        }
        openCamera(cameraFacing, mTextureView.getWidth(), mTextureView.getHeight());
    }

    @Override
    public String openFrontCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; ++i) {
            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            Camera.getCameraInfo(i, cameraInfo);
            //后置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return String.valueOf(i);
            }
        }
        return null;
    }

    @Override
    public String openBackCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; ++i) {
            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            Camera.getCameraInfo(i, cameraInfo);
            //后置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return String.valueOf(i);
            }
        }
        return null;
    }

    @Override
    public void onPause() {
        if (mCamera != null) {
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
        }
    }
}
