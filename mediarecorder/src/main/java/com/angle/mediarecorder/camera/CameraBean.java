package com.angle.mediarecorder.camera;

import android.content.Intent;

/**
 * 摄像头的实体类
 * 这里封装相应的参数
 */
public class CameraBean {

    /**
     * 镜头ID
     */
    public String faceCameraId;
    /**
     * 镜头方向
     */
    public int faceOrientation;

    /**
     * 摄像头方向
     */

    public int cameraOrientation;


    public String getFaceCameraId() {
        return faceCameraId;
    }

    public void setFaceCameraId(String faceCameraId) {
        this.faceCameraId = faceCameraId;
    }

    public int getFaceOrientation() {
        return faceOrientation;
    }

    public void setFaceOrientation(int faceOrientation) {
        this.faceOrientation = faceOrientation;
    }

    public int getCameraOrientation() {
        return cameraOrientation;
    }

    public void setCameraOrientation(int cameraOrientation) {
        this.cameraOrientation = cameraOrientation;
    }
}
