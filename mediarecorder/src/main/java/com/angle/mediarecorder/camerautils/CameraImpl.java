package com.angle.mediarecorder.camerautils;

/**
 * 视频的接口类,主要提供一下功能:
 * 1.打开相机
 * 2.关闭相机(不对外)
 * 3.开启预览
 * 4.关闭预览
 * 5.开启录制
 * 6.停止录制
 * 7.切换相机
 * 8.摄像头检测(一般应该不用)
 * 9.打开前置摄像头
 * 10.打开后置摄像头
 */
public interface CameraImpl {
    String TAG = CameraImpl.class.getSimpleName();

    /**
     * 初始化的时候执行
     */
    void onResume();

    /**
     * 打开相机
     *
     * @param cameraOrientation 摄像头取向
     * @param width             宽度
     * @param height            高度
     * @return 是否成功
     */
    boolean openCamera(int cameraOrientation, int width, int height);

    /**
     * 关闭相机
     */
    void closeCamera();

    /**
     * 开启预览
     *
     * @return 是否成功
     */
    boolean openPreview();

    /**
     * 关闭预览
     */
    void closePreview();

    /**
     * 初始化 MediaRecorder
     *
     * @return 是否成功
     */
    boolean initMediaRecorder();

    /**
     * 开启录制
     *
     * @param path 保存的路径
     * @return 是否成功
     */
    boolean startRecording(String path);

    /**
     * 停止录制
     *
     * @return 是否成功
     */
    void endRecording();

    /**
     * 切换摄像头
     *
     * @param cameraFacing 摄像头方向
     */
    void switchCamera(int cameraFacing);

    /**
     * 打开前置摄像头
     *
     * @return 摄像头的id
     */
    String openFrontCamera();

    /**
     * 打开后置摄像头
     *
     * @return 摄像头的id
     */
    String openBackCamera();

    /**
     * onPause回调
     */
    void onPause();
}
