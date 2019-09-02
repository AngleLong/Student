package com.angle.mediarecorder.camera;

import android.hardware.Camera;
import android.util.Log;

public class CameraUtils {

    private static final String TAG = CameraUtils.class.getSimpleName();

    public static void getCameraCount() {
        int numberOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numberOfCameras; ++i) {
            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            Camera.getCameraInfo(i, cameraInfo);
            //后置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                //后面摄像头的角标
                int faceBackCameraId = i;
                //后面摄像头的角度
                int faceBackCameraOrientation = cameraInfo.orientation;

                Log.e(TAG, "后面摄像头的角标===>" + faceBackCameraId + "后面摄像头的角度===>" + faceBackCameraOrientation);

            }
            //前置摄像头
            else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                int faceFrontCameraId = i;
                int faceFrontCameraOrientation = cameraInfo.orientation;
                Log.e(TAG, "前面摄像头的参数===>" + faceFrontCameraId + "===>" + faceFrontCameraOrientation);

            }
        }
    }
}
