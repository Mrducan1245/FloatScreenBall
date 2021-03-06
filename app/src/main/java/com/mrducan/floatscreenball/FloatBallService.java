package com.mrducan.floatscreenball;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

public class FloatBallService extends Service {

    private LayoutInflater mLayoutInflater;
    private View mFloatBall;
    private WindowManager.LayoutParams mLayoutParams;
    private int mCurrentX;
    private int mCurrentY;
    private static int mFloatViewWidth = 80;
    private static int mFloatViewHeight = 80;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;
    public static int mResultCode = 0;
    public static Intent mResultData = null;
    public static MediaProjectionManager mMediaProjectionManager1 = null;
    private WindowManager mWindowManager = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private DisplayMetrics metrics = null;
    private int mScreenDensity = 0;
    private SimpleDateFormat dateFormat;
    private String strDate;
    private String pathImage;
    private String nameImage;

    public FloatBallService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }





    /**
     * ????????? WindowManager??? LayoutInflater
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        mLayoutInflater = LayoutInflater.from(this);
        createFloatView();
        createVirtualEnvironment();
    }

    private void createFloatView() {
        creatView();
        mFloatBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide the button
                mFloatBall.setVisibility(View.INVISIBLE);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        //start virtual
                        startVirtual();
                    }
                }, 500);

                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    public void run() {
                        //capture the screen
                        startCapture();
                    }
                }, 1500);

                Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    public void run() {
                        mFloatBall.setVisibility(View.VISIBLE);
                        //stopVirtual();
                    }
                }, 1000);
            }
        });

    }


    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualEnvironment() {
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        nameImage = pathImage + strDate + ".png";
        mMediaProjectionManager1 = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatBall != null){
            mWindowManager.removeView(mFloatBall);
        }
        tearDownMediaProjection();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return 1;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection() {
        mResultData = ((MyApplication) getApplication()).getIntent();
        mResultCode = ((MyApplication) getApplication()).getResultCode();
        mMediaProjectionManager1 = ((MyApplication) getApplication()).getmMediaProjectionManager();
        mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCapture() {
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage + strDate + ".png";
        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();

        if (bitmap != null) {
            try {
                File fileImage = new File(nameImage);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(fileImage);
                    media.setData(contentUri);
                    this.sendBroadcast(media);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }


    /**
     * ???????????????
     */
    private void creatView() {
        mFloatBall = mLayoutInflater.inflate(R.layout.sy_floating_view,null);
        mFloatBall.setOnTouchListener(new FloatViewTouchListner());
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.LEFT |Gravity.TOP;
        //??????window type
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //????????????????????????????????????FLAG_NOT_FOCUSABLE??????????????????????????????,??????????????????????????????????????????????????????????????????????????????????????????
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //????????????????????????????????????WindowManager???????????????????????????????????????(x,y)??????
        mCurrentX = mLayoutParams.x = 50;
        mCurrentY = mLayoutParams.y = 50;
        //????????????????????????
        mLayoutParams.width = 100;
        mLayoutParams.height = 100;
        //??????????????????windwo???
        mWindowManager.addView(mFloatBall,mLayoutParams);
    }

    /*?????????????????????????????????????????????????????????(LayoutParams.x,LayoutParams.y)??????*/
    private void updateFloatView() {
        mLayoutParams.x = mCurrentX;
        mLayoutParams.y = mCurrentY;
        mWindowManager.updateViewLayout(mFloatBall, mLayoutParams);
    }

    /*????????????????????????????????????Move??????????????????????????????????????????????????????????????????????????????????????????????????????????????????Activity*/
    private class FloatViewTouchListner implements View.OnTouchListener {
        Intent intent = new Intent(FloatBallService.this,MainActivity.class);
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /**
             * getRawX(),getRawY()???????????????????????????????????????????????????????????????getX(),getY()????????????????????????????????????
             * ???getX(),getY()??????????????????????????????????????????????????????????????????getRawX(),getRawY()???????????????????????????
             * ??????????????????????????????????????????LayoutParams??????x,y??????????????????????????????????????????getRawX(),getRawY()???
             */
            mCurrentX = (int) event.getRawX() - mFloatViewWidth;
            mCurrentY = (int) event.getRawY() - mFloatViewHeight;
            int action = event.getAction();
            switch (action) {
                //??????????????????
                case MotionEvent.ACTION_DOWN:
                    Log.e("MotionEvent","ACTION_DOWN?????????????????????????????????");
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateFloatView();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }
}