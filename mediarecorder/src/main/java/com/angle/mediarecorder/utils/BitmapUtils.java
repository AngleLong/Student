package com.angle.mediarecorder.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class BitmapUtils {

    /**
     * 获取视频文件截图
     * 参考文章:
     * https://blog.csdn.net/bzlj2912009596/article/details/80446256
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */
    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }
}
