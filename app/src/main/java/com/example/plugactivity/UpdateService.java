package com.example.plugactivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

/**
 * 下载安装包的服务类
 * @author david
 */

public class UpdateService extends Service {

    // 文件存储
    private File saveDir;
    private File saveFile;
    private String apkUrl;

    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;

    // 通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    // 下载状态
    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;

    private RemoteViews contentView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        contentView = new RemoteViews(getPackageName(), R.layout.activity_app_update);
        // 获取传值
        String downloadDir = intent.getStringExtra("downloadDir");
        apkUrl = MyApplication.site+intent.getStringExtra("apkUrl");
        // 如果有SD卡,则创建APK文件
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
                .getExternalStorageState())) {

            saveDir = new File(Environment.getExternalStorageDirectory(),
                    downloadDir);
            saveFile = new File(saveDir.getPath(), getResources()
                    .getString(R.string.app_name) + ".apk");
        }

        this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.updateNotification = new Notification();

        // 设置下载过程中，点击通知栏，回到主界面
        updateIntent = new Intent();
        updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
        // 设置通知栏显示内容
        updateNotification.icon = R.drawable.icon_info;
        updateNotification.tickerText = "开始下载";
        updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, 0, true);
        updateNotification.setLatestEventInfo(this,
                getResources().getString(R.string.app_name), "0%",
                updatePendingIntent);
        // 发出通知
        updateNotificationManager.notify(0, updateNotification);
        new Thread(new DownloadThread()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *下载的线程
     */
    private class DownloadThread implements Runnable {
        Message message = updateHandler.obtainMessage();

        public void run() {
            message.what = DOWNLOAD_COMPLETE;
            if (saveDir!=null && !saveDir.exists()) {
                saveDir.mkdirs();
            }
            if (saveFile!=null && !saveFile.exists()) {
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                long downloadSize = downloadFile(apkUrl, saveFile);
                if (downloadSize > 0) {// 下载成功
                    updateHandler.sendMessage(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.what = DOWNLOAD_FAIL;
                updateHandler.sendMessage(message);// 下载失败
            }
        }

        public long downloadFile(String downloadUrl, File saveFile)
                throws Exception {
            int downloadCount = 0;
            int currentSize = 0;
            long totalSize = 0;
            int updateTotalSize = 0;
            int rate = 0;// 下载完成比例

            HttpURLConnection httpConnection = null;
            InputStream is = null;
            FileOutputStream fos = null;

            try {
                URL url = new URL(downloadUrl);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent",
                        "PacificHttpClient");
                if (currentSize > 0) {
                    httpConnection.setRequestProperty("RANGE", "bytes="
                            + currentSize + "-");
                }
                httpConnection.setConnectTimeout(200000);
                httpConnection.setReadTimeout(200000);
                updateTotalSize = httpConnection.getContentLength();//获取文件大小
                if (httpConnection.getResponseCode() == 404) {
                    throw new Exception("fail!");
                }
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(saveFile, false);
                byte buffer[] = new byte[1024 * 1024 * 3];
                int readsize = 0;
                while ((readsize = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, readsize);

                    totalSize += readsize;//已经下载的字节数
                    rate = (int) (totalSize * 100 / updateTotalSize);//当前下载进度
                    // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                    if ((downloadCount == 0) || rate - 0 > downloadCount) {
                        downloadCount += 1;
                        updateNotification.setLatestEventInfo(
                                UpdateService.this, "正在下载", rate + "%",
                                updatePendingIntent);//设置通知的内容、标题等
                        updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, rate, true);
                        updateNotificationManager.notify(0, updateNotification);//把通知发布出去
                    }



                }
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            return totalSize;
        }
    }

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:
                    //当下载完毕，自动安装APK(ps,打电话 发短信的启动界面工作)
                    Uri uri = Uri.fromFile(saveFile);//根据File获得安装包的资源定位符
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);//设置Action
                    installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//新的Activity会在一个新任务打开，而不是在原先的任务栈
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");//设置URI的数据类型
                    startActivity(installIntent);//把打包的Intent传递给startActivity

                    //当下载完毕，更新通知栏，且当点击通知栏时，安装APK
                    updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);
                    updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
                    updateNotification.setLatestEventInfo(UpdateService.this, getResources().getString(R.string.app_name),
                            "下载完成,点击安装", updatePendingIntent);
                    updateNotificationManager.notify(0, updateNotification);

                    // 停止服务
                    stopService(updateIntent);
                    break;
                case DOWNLOAD_FAIL:
                    // 下载失败
                    updateNotification.setLatestEventInfo(UpdateService.this,
                            getResources().getString(R.string.app_name),
                            "下载失败,网络连接超时", updatePendingIntent);
                    updateNotificationManager.notify(0, updateNotification);
                    break;
                default:
                    stopService(updateIntent);
                    break;
            }
        }
    };
}
