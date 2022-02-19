package com.mrducan.floatscreenball;

import android.annotation.SuppressLint;
import android.content.Context;
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

/**
 * 截图模块
 */
public class ScreenShot {

    private static int screenDensity;
    private static int screenWidth;
    private static int screenHeight;

    private static MediaProjection mediaProjection;
    private static VirtualDisplay virtualDisplay;
    private static ImageReader imageReader;
    public static Surface surface;


    //设置媒体项目类
    public static void setUpMediaProjection(MediaProjection outMediaProjection) {
        mediaProjection = outMediaProjection;
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
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
        }
    }

    public static void beginScreenShot(MediaProjection outMediaProjection,Context context,Boolean ifSaveImage) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beginVirtual(outMediaProjection);
            }
        }, 0);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beginCapture(outMediaProjection,context,ifSaveImage);
            }
        }, 150);
    }

    private static void beginVirtual(MediaProjection outMediaProjection) {
        if (null != mediaProjection) {
            virtualDisplay();
        } else {
            setUpMediaProjection(outMediaProjection);
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

    private static void beginCapture(MediaProjection outMediaProjection,Context context,Boolean ifSaveImage) {
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
            beginScreenShot(outMediaProjection,context,ifSaveImage);
        } else {
            SaveTask saveTask = new SaveTask(context,ifSaveImage);
            AsyncTaskCompat.executeParallel(saveTask, acquireLatestImage);

            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    releaseVirtual();
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
    public static void stopMediaProjection() {
        if (null != mediaProjection) {
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

}





