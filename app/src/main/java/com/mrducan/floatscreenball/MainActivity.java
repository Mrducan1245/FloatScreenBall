package com.mrducan.floatscreenball;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
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

    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startIntent();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startIntent() {
        if (intent != null && result != 0) {
            ((MyApplication) getApplication()).setResultCode(result);
            ((MyApplication) getApplication()).setIntent(intent);
            Intent intent = new Intent(getApplicationContext(), FloatBallService.class);
            startService(intent);
        } else {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            ((MyApplication) getApplication()).setmMediaProjectionManager(mMediaProjectionManager);
        }
    }

    //在onactivity里处理用户的选择进行处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            } else if (data != null && resultCode != 0) {
                result = resultCode;
                intent = data;
                ((MyApplication) getApplication()).setResultCode(resultCode);
                ((MyApplication) getApplication()).setIntent(data);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), FloatBallService.class);
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