package com.mrducan.floatscreenball;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

public class PhotoUtil {

    private static VirtualDisplay virtualDisplay;
    private static Surface surface;
    private static ImageReader imageReader;
    private static Image image;


    @SuppressLint("WrongConstant")
    public static void screenShot(WindowManager windowManager, MediaProjection mediaProjection) {
//        View view = activity.getWindow().getDecorView();
//
//        //允许当前窗口保存缓存信息
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//
//        //获取状态栏高度
//        Rect rect = new Rect();
//        view.getWindowVisibleDisplayFrame(rect);
//        int statusBarHeight = rect.top;

        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        int dpi = outMetrics.densityDpi;

        Log.e("screenShot","width:" + width + "height:"+height + "dpi:"+dpi);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            imageReader = ImageReader.newInstance(
                    width,
                    height,
                    PixelFormat.RGBA_8888, 2);
            Log.e("screenShot","创建了imageReader:"+imageReader.toString());


            surface = imageReader.getSurface();
            if (surface == null){
                Log.e("screenShot","surface为空");
            }
            Log.e("screenShot","创建了imageReader.getSurface():"+surface.toString());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                virtualDisplay = mediaProjection.createVirtualDisplay("screenShot",
                        width,height,
                        dpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        surface,null,null);
                Log.e("screenShot","创建了virtualDisplay："+virtualDisplay.toString());
            }

            //设置延时收集照片，不然照片为空
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   image = imageReader.acquireLatestImage();
                   Log.e("screenShot","调用了imageReader.acquireLatestImage()");

                   if (image == null) {
                       Log.e("screenShot","没有图片所以跳过了");
                       return;
                   }
                   Image.Plane[] planes = image.getPlanes();
                   ByteBuffer buffer = planes[0].getBuffer();
                   int pixelStride = planes[0].getPixelStride();
                   int rowStride = planes[0].getRowStride();
                   int rowPadding = rowStride - pixelStride * width;

                   Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride,
                           height, Bitmap.Config.ARGB_8888);
                   bitmap.copyPixelsFromBuffer(buffer);

                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                       saveBitmapToLocal(bitmap);
                   }
                   image.close();

               }
           },500);

        }
    }

    //将图片保存到本地
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void saveBitmapToLocal(Bitmap bitmap) {

        try {
            Date currentDate = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmmss");
            String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots";
            File file = new File(FILE_PATH, date + ".png");
            Log.e("MotionEvent","地址是"+FILE_PATH);

            // file其实是图片，它的父级File是文件夹，判断一下文件夹是否存在，如果不存在，创建文件夹
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) { // 文件夹不存在
                fileParent.mkdirs();// 创建文件夹
            }

            // 将图片保存到本地
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            Log.e("MotionEvent","图片转化成功，");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

