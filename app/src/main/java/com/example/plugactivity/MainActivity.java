package com.example.plugactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.example.plugactivity.service.DownloadService;

public class MainActivity extends AppCompatActivity {
    public static final String APP_PACKAGE_NAME = "bsd_purang.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadService.startDownloadService(MainActivity.this,
                "https://yanyangtian.purang.com/download/" + APP_PACKAGE_NAME,
                APP_PACKAGE_NAME);
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
