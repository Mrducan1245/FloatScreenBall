package com.mrducan.floatscreenball;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

public class ScreenShot {

    private static WindowManager windowManager;
    private static int screenDensity;
    private static int screenWidth;
    private static int screenHeight;

    private static MediaProjection mediaProjection;
    private static VirtualDisplay virtualDisplay;
    private static ImageReader imageReader;
    public static Surface surface;

    //设置媒体项目类
    public static void setUpMediaProjection(Service service, Intent scIntent,MyApplication application) {
        if (scIntent == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            service.startActivity(intent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection = application.getMediaProjection();
            }
        }
    }



    public static void getWH(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.densityDpi;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    @SuppressLint("WrongConstant")
    public static void createImageReader() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
        }
    }

    public static void beginScreenShot(final Service service, final Intent intent,MyApplication application) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beginVirtual(service, intent,application);
            }
        }, 0);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beginCapture(service, intent,application);
            }
        }, 150);
    }

    private static void beginVirtual(Service service, Intent intent,MyApplication application) {
        if (null != mediaProjection) {
            virtualDisplay();
        } else {
            setUpMediaProjection(service, intent,application);
            virtualDisplay();
        }
    }

    private static void virtualDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            surface = imageReader.getSurface();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            virtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror", screenWidth,
                    screenHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, surface,
                    null, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static MediaProjectionManager getMediaProjectionManager(MyApplication application) {
        return (MediaProjectionManager) application.getMediaProjectionManager();

    }

    private static void beginCapture(Service service, Intent intent,MyApplication application) {
        Image acquireLatestImage = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                acquireLatestImage = imageReader.acquireLatestImage();
            }
        } catch (IllegalStateException e) {
            if (null != acquireLatestImage) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    acquireLatestImage.close();
                    acquireLatestImage = null;
                    acquireLatestImage = imageReader.acquireLatestImage();
                }

            }
        }

        if (acquireLatestImage == null) {
            beginScreenShot(service, intent,application);
        } else {
            SaveTask saveTask = new SaveTask();
          AsyncTaskCompat.executeParallel(saveTask, acquireLatestImage);

            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    releaseVirtual();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        stopMediaProjection();
                    }
                }
            }, 1000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void releaseVirtual() {
        if (null != virtualDisplay) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void stopMediaProjection() {
        if (null != mediaProjection) {
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

}





