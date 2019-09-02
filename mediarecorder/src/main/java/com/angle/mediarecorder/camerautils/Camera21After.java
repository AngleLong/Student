package com.angle.mediarecorder.camerautils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.angle.mediarecorder.AutoFitTextureView;
import com.angle.mediarecorder.CameraActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 21版本之后的摄像头
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera21After implements CameraImpl {

    private Activity mActivity;

    private AutoFitTextureView mTextureView;

    private CameraManager mCameraManager;

    private Size mVideoSize;

    private Size mPreviewSize;
    /**
     * 屏幕方向
     */
    private Integer mSensorOrientation;

    private MediaRecorder mMediaRecorder;

    private CameraDevice mCameraDevice;

    private CaptureRequest.Builder mCaptureRequest;

    private CameraCaptureSession mPreviewSession;

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
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


    /**
     * 回调对象
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            //开启预览
            openPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
        }

    };
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private String mNextVideoAbsolutePath;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            openCamera(CameraCharacteristics.LENS_FACING_BACK, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    public Camera21After(Activity activity, AutoFitTextureView textureView) {
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        mActivity = activity;
        mTextureView = textureView;
    }

    @Override
    public void onResume() {
        //创建一个线程
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

        if (mTextureView.isAvailable()) {
            //如果可用的话直接打开Camera
            openCamera(CameraCharacteristics.LENS_FACING_BACK, mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            //设置相应的监听
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean openCamera(int cameraOrientation, int width, int height) {
        String cameraId;
        if (CameraCharacteristics.LENS_FACING_BACK == cameraOrientation) {
            cameraId = openBackCamera();
        } else {
            cameraId = openFrontCamera();
        }

        if (cameraId == null) {
            Log.e(TAG, "获取前后摄像头失败");
            return false;
        }

        try {
            // 设置相机特性
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
            // 获取预览画面的尺寸
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //拍照方向
            mSensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

            if (streamConfigurationMap == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }

            //获取屏幕尺寸
            mVideoSize = chooseVideoSize(streamConfigurationMap.getOutputSizes(MediaRecorder.class));
            //获取预览尺寸
            mPreviewSize = chooseOptimalSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);

            //设置方向的
            int orientation = mActivity.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(width, height);

            //创建MediaRecord对象
            mMediaRecorder = new MediaRecorder();

            mCameraManager.openCamera(cameraId, mStateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置视频的尺寸,这里处理的是4:3的视频并且视频不能超过1080p否则MediaRecorder处理不了
     *
     * @param choices 可用尺寸列表
     * @return 视频的尺寸
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }


    /**
     * 获取预览尺寸
     *
     * @param choices     尺寸集合
     * @param width       宽度
     * @param height      高度
     * @param aspectRatio 宽高比
     * @return 预览尺寸
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    @Override
    public void closeCamera() {
        try {
            closePreview();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
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
        if (null == mCameraDevice || !mTextureView.isAvailable()) {
            return false;
        }

        //关闭之前有的预览效果
        closePreview();
        //获取surface
        SurfaceTexture texture = mTextureView.getSurfaceTexture();

        //开启预览
        try {
            mCaptureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            //准备预览
            Surface previewSurface = new Surface(texture);
            mCaptureRequest.addTarget(previewSurface);

            //开启预览
            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            mPreviewSession = cameraCaptureSession;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, mBackgroundHandler);
            return true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 更新预览
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }

        mCaptureRequest.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mPreviewSession.setRepeatingRequest(mCaptureRequest.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closePreview() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    @Override
    public boolean initMediaRecorder() {
        try {
            //设置音频来源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置视频来源
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            //设置输出格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //设置路径
            if (TextUtils.isEmpty(mNextVideoAbsolutePath)) {
                mNextVideoAbsolutePath = getVideoFilePath(mActivity);
            }
            mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
            mMediaRecorder.setVideoEncodingBitRate(10000000);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return false;
        }

        if (TextUtils.isEmpty(path)) {
            mNextVideoAbsolutePath = getVideoFilePath(mActivity);
        } else {
            mNextVideoAbsolutePath = path;
        }

        try {
            closePreview();
            initMediaRecorder();

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mCaptureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();


            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mCaptureRequest.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mCaptureRequest.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 开始录制
                            mMediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);
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
        closeCamera();
        openCamera(cameraFacing, mTextureView.getWidth(), mTextureView.getHeight());
    }

    @Override
    public String openFrontCamera() {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            for (String id : cameraIdList) {

                //获取摄像头参数
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
                //获取摄像头方向的
                int orientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                //如果是前置摄像头,那么就直接返回
                if (orientation == CameraCharacteristics.LENS_FACING_FRONT) {
                    return id;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "获取摄像头异常");
        }

        return "";
    }

    @Override
    public String openBackCamera() {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            for (String id : cameraIdList) {

                //获取摄像头参数
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
                //获取摄像头方向的
                int orientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                //如果是前置摄像头,那么就直接返回
                if (orientation == CameraCharacteristics.LENS_FACING_BACK) {
                    return id;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "获取摄像头异常");
        }

        return "";
    }

    @Override
    public void onPause() {

    }
}
