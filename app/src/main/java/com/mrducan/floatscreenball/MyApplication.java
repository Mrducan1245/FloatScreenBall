package com.mrducan.floatscreenball;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

public class MyApplication extends Application {

    Intent intent;
    int resultCode;
    private MediaProjectionManager mMediaProjectionManager;

    public MediaProjectionManager getmMediaProjectionManager() {
        return mMediaProjectionManager;
    }

    public void setmMediaProjectionManager(MediaProjectionManager mMediaProjectionManager) {
        this.mMediaProjectionManager = mMediaProjectionManager;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}