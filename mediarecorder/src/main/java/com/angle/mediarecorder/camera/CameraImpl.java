package com.angle.mediarecorder.camera;

import android.content.Context;

/**
 * 因为相机存在相应的适配
 * 5.0版本之前使用Camera进行相机的操作
 * 5.0之后使用Camera2(CameraManager)进行相机操作
 * 所以这里定义了一个接口封装相应的内容
 */
public interface CameraImpl {

    String TAG = CameraImpl.class.getSimpleName();

    /**
     * 打开相机的操作
     *
     * @return 是否打开成功
     */
    boolean openCamera();

    /**
     * 获取前置摄像头参数
     *
     * @param orientation       前后的参数控制
     * @return 返回相应的参数
     */
    CameraBean getCameraPara( int orientation);


    /**
     * 获取摄像头个数
     *
     * @return 摄像头个数
     */
    int getCameraCount();
}
