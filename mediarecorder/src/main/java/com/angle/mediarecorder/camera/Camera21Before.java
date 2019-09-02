package com.angle.mediarecorder.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Arrays;

/**
 * @author hejinlong
 * 5.0之前的实现类
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera21Before implements CameraImpl {

    private final CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mCaptureRequest;


    public Camera21Before(Context context) {
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public boolean openCamera(final SurfaceHolder surfaceHolder) {
        final CameraBean cameraBean = getCameraPara(CameraCharacteristics.LENS_FACING_BACK);
        try {
            mCameraManager.openCamera(cameraBean.getFaceCameraId(), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    Log.e(TAG, "摄像头打开");
                    mCameraDevice = cameraDevice;
                    startPreview(surfaceHolder);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                    Log.e(TAG, "摄像头断开连接");
                    cameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {
                    Log.e(TAG, "摄像头异常");
                    cameraDevice.close();
                }
            }, null);
            return true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean openCamera() {
        return false;
    }

    @Override
    public CameraBean getCameraPara(int orientation) {
        try {
            //获取摄像头列表
            String[] cameraIdList = mCameraManager.getCameraIdList();

            CameraBean cameraBean = new CameraBean();
            for (String id : cameraIdList) {

                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
                //获取摄像头方向的
                //int orientation = characteristics.get(CameraCharacteristics.LENS_FACING);

                cameraBean.setFaceCameraId(id);
                cameraBean.setFaceOrientation(characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));

                if (orientation == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraBean.setCameraOrientation(CameraCharacteristics.LENS_FACING_FRONT);
                    return cameraBean;
                } else {
                    cameraBean.setCameraOrientation(CameraCharacteristics.LENS_FACING_BACK);
                    return cameraBean;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "相机访问失败" + e.toString());
        }

        return null;
    }

    @Override
    public int getCameraCount() {
        try {
            return mCameraManager.getCameraIdList().length;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "相机访问失败" + e.toString());
            return 0;
        }
    }

    public void startPreview(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "startPreview: "+(mCameraDevice==null) );
        if (mCameraDevice != null) {
            Log.e(TAG, "startPreview: " );
            try {
                mCaptureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mCaptureRequest.addTarget(surfaceHolder.getSurface());

                mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                Log.e(TAG, "onConfigured: " );

                                mCaptureRequest.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                mCaptureRequest.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                try {
                                    cameraCaptureSession.setRepeatingRequest(mCaptureRequest.build(), null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                                Log.e(TAG, "onConfigureFailed: " );
                            }
                        },null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
