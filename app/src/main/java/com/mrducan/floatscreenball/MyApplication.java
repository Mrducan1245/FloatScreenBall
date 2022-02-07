package com.mrducan.floatscreenball;

import android.app.Application;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

public class MyApplication extends Application {
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;

    public MediaProjection getMediaProjection() {
        return mediaProjection;
    }

    public void setMediaProjection(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
    }

    public MediaProjectionManager getMediaProjectionManager() {
        return mediaProjectionManager;
    }

    public void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        this.mediaProjectionManager = mediaProjectionManager;
    }
}
