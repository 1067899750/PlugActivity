package com.example.plugactivity.download;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.plugactivity.R;
import com.example.plugactivity.service.IntentUtils;
import com.example.plugactivity.service.NotificationClickReceiver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.core.app.NotificationCompat;

/**
 * @author puyantao
 * @description
 * @date 2020/2/27 10:42
 */
public class DownloadTaskService extends Service {
    /**
     * appUrl
     */
    public static final String KEY_DOWN_URL = "Key_Down_Url";
    private DownloadTask mDownloadTask;
    private String downloadUrl;
    protected static final String CHANNEL_ID = "android_download_apk_notification";

    /**
     * 通知
     */
    private NotificationManager notificationManager;
    /**
     * 下载通知进度提示
     */
    private Notification notification;
    private NotificationCompat.Builder builder;
    private final int notificationId = 1;
    /**
     * 定义Map来保存Notification对象
     */
    private Map<Integer, Notification> map = new HashMap<Integer, Notification>();

    DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            notification.contentView.setProgressBar(R.id.pbProgress, 100, progress, false);

            String progressData = "正在更新：" + progress + "%";
            notification.contentView.setTextViewText(R.id.tvUpdate, progressData);
            notification = builder.build();
            notificationManager.notify(notificationId, notification);
        }


        @Override
        public void onSuccess() {
            installApk();
            mDownloadTask = null;
            //下载成功时将前台服务通知关闭，比创建下载成功通知
            notification.contentView.setTextViewText(R.id.tvUpdate, "下载成功");
            notificationManager.notify(notificationId, builder.build());
            notificationManager.cancel(notificationId);
            map.remove(notificationId);
            stopSelf();
            Toast.makeText(DownloadTaskService.this, "Download Success", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed() {
            mDownloadTask = null;
            //下载失败时将前台服务通知关闭，比创建下载成功通知
            builder.setAutoCancel(true);
            notificationManager.cancel(notificationId);
            map.remove(notificationId);
            stopSelf();
            Toast.makeText(DownloadTaskService.this, "Download Failed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPaused() {
            mDownloadTask = null;
            stopSelf();
            Toast.makeText(DownloadTaskService.this, "Paused", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCanceled() {
            mDownloadTask = null;
            stopSelf();
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

    public class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (mDownloadTask != null) {
                downloadUrl = url;
                createServiceInfo();
                Toast.makeText(DownloadTaskService.this, "Download...", Toast.LENGTH_LONG).show();
            }
        }

        public void pauseDownload() {
            if (mDownloadTask != null) {
                mDownloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (mDownloadTask != null) {
                mDownloadTask.cancelDownload();
            }
            if (downloadUrl != null) {
                //取消下载时需要将文件删除，关闭通知
                deleteApkFile();
                builder.setAutoCancel(true);
                notificationManager.cancel(notificationId);
                map.remove(notificationId);
                stopSelf();
                Toast.makeText(DownloadTaskService.this, "Canceled", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            downloadUrl = intent.getStringExtra(KEY_DOWN_URL);
        }
//        startForeground(1, getNotification("Download...", 0));
        Toast.makeText(DownloadTaskService.this, "Download...", Toast.LENGTH_LONG).show();

        createServiceInfo();
        return super.onStartCommand(intent, flags, startId);
    }


    private void createServiceInfo(){
        if (!map.containsKey(notificationId)) {
            mDownloadTask = new DownloadTask(mDownloadListener);
            mDownloadTask.execute(downloadUrl);

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager);
            }
            builder = getNotificationBuilder();
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification);
            remoteViews.setTextViewText(R.id.tvDownloadNoticeName, "断点下载");
            builder.setContent(remoteViews);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                builder.setCustomBigContentView(remoteViews);
            }

            notification = builder.build();
            notificationManager.notify(notificationId, notification);
            map.put(notificationId, notification);
            Toast.makeText(this, "开始升级", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 方法描述：createNotification方法
     *
     * @param
     * @return
     */

    public NotificationCompat.Builder getNotificationBuilder() {
        Intent activityIntent = new Intent(this, NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, activityIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知的优先级：最大
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                //设置跳转应用
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0})
                .setSound(null)
                .setAutoCancel(false);

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


    private void installApk() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , downloadUrl.substring(downloadUrl.lastIndexOf("/")));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setDataAndType(IntentUtils.getFileUri(intent, this, file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     *  删除安装包
     */
    private void deleteApkFile(){
        if (downloadUrl != null){
            String filedName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory + filedName);
            if (file.exists()) {
                file.delete();
            }
        }
    }


}

















