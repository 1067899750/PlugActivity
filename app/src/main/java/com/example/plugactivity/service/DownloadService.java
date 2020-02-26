package com.example.plugactivity.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.plugactivity.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


/**
 * @author puyantao
 * @describe APP下载更新服务
 * @create 2020/2/24 14:14
 */
public class DownloadService extends Service {
    public static final String TAG = DownloadService.class.getSimpleName();
    /**
     * handler状态
     */
    public static final int DOWN_SUCCESS = 1;
    public static final int DOWN_LOADING = 2;
    public static final int DOWN_ERROR = 3;
    /**
     * 通知
     */
    private NotificationManager notificationManager;
    /**
     * 下载通知进度提示
     */
    private Notification notification;
    private NotificationCompat.Builder builder;
    /**
     * app名称
     */
    private String appName;
    /**
     * appUrl
     */
    private static String downUrl;
    /**
     * appUrl
     */
    public static final String KEY_DOWN_URL = "Key_Down_Url";
    /**
     * app名称
     */
    public static final String KEY_APP_NAME = "Key_App_Name";
    private final int notificationId = 1;
    /**
     * 定义Map来保存Notification对象
     */
    private Map<Integer, Notification> map = new HashMap<Integer, Notification>();
    protected static final String CHANNEL_ID = "android_download_apk_notification";


    public static void startDownloadService(Context context, String url, String name) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(KEY_DOWN_URL, url);
        intent.putExtra(KEY_APP_NAME, name);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            appName = intent.getStringExtra(KEY_APP_NAME);
            downUrl = intent.getStringExtra(KEY_DOWN_URL);
        }
        if (!map.containsKey(notificationId)) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager);
            }
            builder = getNotificationBuilder();
            Intent activityIntent = new Intent(this, NotificationClickReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, activityIntent, 0);
            builder.setContentIntent(pendingIntent);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification);
            remoteViews.setTextViewText(R.id.tvDownloadNoticeName, "应用名字");
            builder.setContent(remoteViews);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                builder.setCustomBigContentView(remoteViews);
            }

            notification = builder.build();
            notificationManager.notify(notificationId, notification);
            map.put(notificationId, notification);
            Toast.makeText(this, "开始升级", Toast.LENGTH_LONG).show();
            new Thread(new UpLoadDataThread()).start();
            createThread();
        }
        return super.onStartCommand(intent, flags, startId);
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

    /**
     * 方法描述：createNotification方法
     *
     * @param
     * @return
     * @see DownloadService
     */

    public NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL_ID)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知的优先级：最大
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setVibrate(new long[]{0})
                .setSound(null)
                .setAutoCancel(false);

    }

    /**
     * 方法描述：createThread方法, 开线程下载
     *
     * @param
     * @return
     * @see DownloadService
     */
    public void createThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadUpdateFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(DOWN_ERROR);
                }
            }
        }).start();
    }


    /**
     * 下载安装包
     */
    public void downloadUpdateFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
            downApkError();
        }
        InputStream is = null;
        FileOutputStream fileOutputStream = null;
        try {
            HttpURLConnection connection = getConnection(downUrl);
            long length = connection.getContentLength();
            is = connection.getInputStream();
            if (is != null) {
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File downloaded = new File(folder, appName);
                fileOutputStream = new FileOutputStream(downloaded);
                byte[] b = new byte[1024];
                int charb = -1;
                int count = 0;
                int timers = 0;
                while ((charb = is.read(b)) != -1) {
                    fileOutputStream.write(b, 0, charb);
                    count += charb;
                    String s = Thread.currentThread().getName();
                    int progress = (int) (((float) count / length) * 100);
                    // 为了防止频繁的通知导致应用吃紧，百分比增加1才通知一次
                    if (progress - timers == 1 || progress == 100) {
                        Log.i(TAG, "--->" + count + "/" + length + " = " + progress);
                        timers++;
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putInt("progress", progress);
                        message.what = DOWN_LOADING;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                }
            }
            fileOutputStream.flush();
            handler.sendEmptyMessage(DOWN_SUCCESS);
        } catch (IOException ioe) {
            Log.i(TAG, "Failed to connect " + downUrl);
            handler.sendEmptyMessage(DOWN_ERROR);
        } catch (Exception e) {
            Log.i(TAG, "Failed to download file:" + e.toString());
            handler.sendEmptyMessage(DOWN_ERROR);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                Log.i(TAG, "Failed to close file output stream");
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.i(TAG, "Failed to close file");
            }
        }
    }

    private Handler handler;
    class UpLoadDataThread implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case DOWN_SUCCESS:
                            downApkSuccess();
                            break;
                        case DOWN_LOADING:
                            downApkLoading(msg.getData().getInt("progress"));
                            break;
                        case DOWN_ERROR:
                            downApkError();
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }


    /**
     * 更新通知
     *
     * @param progress
     */
    private void downApkLoading(int progress) {
        String s = Thread.currentThread().getName();
        notification.contentView.setProgressBar(R.id.pbProgress, 100, progress, false);

        String progressData = "正在更新：" + progress + "%";
        notification.contentView.setTextViewText(R.id.tvUpdate, progressData);
        notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }

    /**
     * 下载成功
     */
    private void downApkSuccess() {
        installApk();
        notification.contentView.setTextViewText(R.id.tvUpdate, "下载成功");
        notificationManager.notify(notificationId, builder.build());
        notificationManager.cancel(notificationId);
        map.remove(notificationId);
        stopSelf();
    }

    /**
     * 下载错误
     */
    private void downApkError() {
        Toast.makeText(this, "下载失败", Toast.LENGTH_LONG).show();
        builder.setAutoCancel(true);
        notificationManager.cancel(notificationId);
        map.remove(notificationId);
        stopSelf();
    }


    public static HttpURLConnection getConnection(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.connect();
        return connection;
    }

    private void installApk() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , appName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setDataAndType(IntentUtils.getFileUri(intent, this, file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }


}

