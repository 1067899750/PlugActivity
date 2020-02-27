package com.example.plugactivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import com.example.plugactivity.download.DownloadTaskService;
import com.example.plugactivity.permission.CommonPermissionHelper;
import com.example.plugactivity.permission.PermissionDialogBean;
import com.example.plugactivity.permission.PermissionUtils;
import com.yanzhenjie.permission.runtime.Permission;


public class MainActivity extends AppCompatActivity {
    public static final String APP_PACKAGE_NAME = "bsd_purang.apk";
    public static final String url = "https://yanyangtian.purang.com/download/bsd_purang.apk";
    private DownloadTaskService.DownloadBinder mDownloadBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadBinder = (DownloadTaskService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
//        DownloadService.startDownloadService(MainActivity.this,
//                "https://yanyangtian.purang.com/download/" + APP_PACKAGE_NAME,
//                APP_PACKAGE_NAME);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //检验是否获取权限，如果获取权限，外部存储会处于开放状态，会弹出一个toast提示获得授权
                    String sdCard = Environment.getExternalStorageState();
                    if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(this, "获得授权", Toast.LENGTH_LONG).show();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "buxing", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onDownload(View view) {
       verifyStoragePermissions(MainActivity.this);

        PermissionDialogBean bean = CommonPermissionHelper.getSDPermission();
        PermissionUtils.requestPermission(this, bean, new PermissionUtils.OnPermissionBack() {
            @Override
            public void onResult(boolean b) {
                if (b) {//成功
                    Intent intent = new Intent(MainActivity.this, DownloadTaskService.class);
                    intent.putExtra(DownloadTaskService.KEY_DOWN_URL, url);
                    startService(intent);
                }
            }

            @Override
            public void cancelDialog() {

            }

            @Override
            public void comeBack() {

            }
        }, Permission.Group.STORAGE);


//        if (PermissionUtil.isGrantExternalRW(MainActivity.this, 1)) {
//            Intent intent = new Intent(MainActivity.this, DownloadTaskService.class);
//            startActivity(intent);
//            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
//            mDownloadBinder.startDownload(url);
//        }

    }
}





















