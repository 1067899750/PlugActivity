package com.example.plugactivity.download;

/**
 * @author puyantao
 * @describe
 * @create 2020/2/27 8:30
 */
public interface DownloadListener {
    /**
     *  下载进度
     * @param progress
     */
    void onProgress(int progress);

    /**
     * 下载成功
     */
    void onSuccess();

    /**
     * 下载失败
     */
    void onFailed();

    /**
     * 暂停下载
     */
    void onPaused();

    /**
     * 取消下载
     */
    void onCanceled();
}





