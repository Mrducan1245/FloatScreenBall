package com.mrducan.floatscreenball;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private MyApplication myApplication;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApplication = new MyApplication();
        FloatBallService.application = myApplication;

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, 333);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333 && data != null){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                myApplication.setMediaProjectionManager(mediaProjectionManager);
                myApplication.setMediaProjection(mediaProjectionManager.getMediaProjection(resultCode,data));
                Log.e("onActivity","成功把MediaProjectionManager和MediaProjection赋给MyApplication");
            }
        }
    }



    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, FloatBallService.class);
        String TAG = "MainActivity";
        switch (view.getId()){
            case R.id.start_btn:
                startService(intent);
                break;
            case R.id.show_btn:
                Log.e(TAG, "show_btn");
                break;
            case R.id.hide_btn:
                Log.e(TAG, "hide_btn");
                break;
            case R.id.close_btn:
                stopService(intent);
                break;
        }
    }
}