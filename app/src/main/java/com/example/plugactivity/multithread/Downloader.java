package com.example.plugactivity.multithread;

import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/4 9:05
 */
public class Downloader {

    public static void main(String[] argc) {
        try {
            //设置URL的地址和下载后的文件名
            String filename = "bsd_purang.apk";
            String path = "https://yytuatbranch.purang.com/download/bsd_purang.apk";
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            //获得需要下载的文件的长度(大小)
            int fileLength = connection.getContentLength();
            System.out.println("要下载的文件长度" + fileLength);
            //生成一个大小相同的本地文件, 下载到AS的根目录中， 下在到磁盘中用"D:/test/文件名"
            RandomAccessFile file = new RandomAccessFile(filename, "rwd");
            file.setLength(fileLength);
            file.close();
            connection.disconnect();
            //设置有多少条线程下载
            int threadSize = 3;
            //计算每个线程下载的量
            int threadLength = fileLength % 3 == 0 ? fileLength / 3 : fileLength / 3 + 1;
            for (int i = 0; i < threadSize; i++) {
                //设置每条线程从哪个位置开始下载
                int startPosition = i * threadLength;
                //从文件的什么位置开始写入数据
                RandomAccessFile threadFile = new RandomAccessFile(filename, "rwd");
                threadFile.seek(startPosition);
                //启动三条线程分别从startPosition位置开始下载文件
                new DownLoadThread(i, startPosition, threadFile, threadLength, path).start();
            }
            int quit = System.in.read();
            while ('q' != quit) {
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}




























