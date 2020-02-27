package com.example.plugactivity.download;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.example.plugactivity.R;

import java.io.File;

import androidx.core.app.NotificationCompat;

/**
 * 
 * @description
 * @author puyantao
 * @date 2020/2/27 10:42
 */
public class DownloadTaskService extends Service {
    private  DownloadTask mDownloadTask;
    private String downloadUrl;
    protected static final String CHANNEL_ID = "android_download_apk_notification";
    DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Download...", progress));
        }


        @Override
        public void onSuccess() {
            mDownloadTask = null;
            //下载成功时将前台服务通知关闭，比创建下载成功通知
            stopForeground(true);
            getNotificationManager().notify(1,  getNotification("Download...", -1));
            Toast.makeText(DownloadTaskService.this, "Download Success", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed() {
            mDownloadTask = null;
            //下载失败时将前台服务通知关闭，比创建下载成功通知
            stopForeground(true);
            getNotificationManager().notify(1,  getNotification("Download...", -1));
            Toast.makeText(DownloadTaskService.this, "Download Failed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPaused() {
            mDownloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadTaskService.this, "Paused", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCanceled() {
            mDownloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadTaskService.this, "Canceled", Toast.LENGTH_LONG).show();
        }
    };


    public DownloadTaskService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder{
        public void startDownload(String url){
            if (mDownloadTask != null){
                downloadUrl = url;
                mDownloadTask = new DownloadTask(mDownloadListener);
                mDownloadTask.execute(downloadUrl);
                startForeground(1, getNotification("Download...", 0));
                Toast.makeText(DownloadTaskService.this, "Download...", Toast.LENGTH_LONG).show();
            }
        }

        public void pauseDownload(){
            if (mDownloadTask != null){
                mDownloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if (mDownloadTask != null){
                mDownloadTask.cancelDownload();
            }
            if (downloadUrl != null){
                //取消下载时需要将文件删除，关闭通知
                String filedName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + filedName);
                if (file.exists()){
                    file.delete();
                }
                getNotificationManager().cancel(1);
                stopForeground(true);
                Toast.makeText(DownloadTaskService.this, "Canceled", Toast.LENGTH_LONG).show();
            }
        }


    }


    private Notification getNotification(String title, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title);
        if (progress >= 0){
            //当progress大于或等于0时显示进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }


    private NotificationManager getNotificationManager() {
        NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        // 通知渠道
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "应用更新", NotificationManager.IMPORTANCE_HIGH);
        //是否绕过请勿打扰模式
        channel.canBypassDnd();
        // 开启指示灯，如果设备有的话。
        channel.enableLights(false);
        //关闭震动
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        //是否会有灯光
        channel.shouldShowLights();
        //获取系统通知响铃声音的配置
        channel.getAudioAttributes();
        //  设置指示灯颜色
        channel.setLightColor(Color.RED);
        // 设置是否应在锁定屏幕上显示此频道的通知
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // 设置是否显示角标
        channel.setShowBadge(true);
        //  设置绕过免打扰模式
        channel.setBypassDnd(true);
        //最后在notificationManager中创建该通知渠道
        notificationManager.createNotificationChannel(channel);
    }


}

















