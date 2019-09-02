package com.angle.mediarecorder.camera;


import android.hardware.Camera;

public class Camera21After implements CameraImpl {


    @Override
    public boolean openCamera() {
        return false;
    }

    @Override
    public CameraBean getCameraPara(int orientation) {
        int numberOfCameras = Camera.getNumberOfCameras();

        CameraBean cameraBean = new CameraBean();
        for (int i = 0; i < numberOfCameras; ++i) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            Camera.getCameraInfo(i, cameraInfo);

            cameraBean.setFaceOrientation(cameraInfo.orientation);
            cameraBean.setFaceCameraId(String.valueOf(i));

            if (orientation == Camera.CameraInfo.CAMERA_FACING_BACK) {
                //后置摄像头
                cameraBean.setCameraOrientation(Camera.CameraInfo.CAMERA_FACING_BACK);
                return cameraBean;
            } else if (orientation == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                //前置摄像头
                cameraBean.setCameraOrientation(Camera.CameraInfo.CAMERA_FACING_FRONT);
                return cameraBean;
            }
        }
        return null;
    }

    @Override
    public int getCameraCount() {
        return Camera.getNumberOfCameras();
    }
}
