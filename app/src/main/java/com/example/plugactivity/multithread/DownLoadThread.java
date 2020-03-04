package com.example.plugactivity.multithread;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/4 9:05
 */
public class DownLoadThread extends Thread {
    private int threadId;
    private int startPosition;
    private RandomAccessFile threadFile;
    private int threadLength;
    private String path;

    public DownLoadThread(int threadId, int startPosition,
                          RandomAccessFile threadFile, int threadLength, String path) {
        this.threadId = threadId;
        this.startPosition = startPosition;
        this.threadFile = threadFile;
        this.threadLength = threadLength;
        this.path = path;
    }

    public DownLoadThread() {
    }

    @Override
    public void run() {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //指定从什么位置开始下载
            connection.setRequestProperty("Range", "bytes=" + startPosition + "-");
            if (connection.getResponseCode() == 206) {
                InputStream is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                int length = 0;
                while (length < threadLength && (len = is.read(buffer)) != -1) {
                    threadFile.read(buffer, 0, len);
                    //计算累计下载的长度
                    length += len;
                }
                threadFile.close();
                is.close();
                System.out.println("线程"+(threadId +1) + "已下载完成");
            }

        } catch (Exception e) {
            System.out.println("线程"+(threadId +1) + "下载出错"+ e);
        }
    }
}


















