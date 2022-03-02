package com.mrducan.floatscreenball;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

public class FloatBallService extends Service {

    private LayoutInflater mLayoutInflater;
    private View mFloatBall;
    private WindowManager.LayoutParams mLayoutParams;
    private int mCurrentX;
    private int mCurrentY;
    private int mFloatViewWidth = 80;
    private int mFloatViewHeight = 80;
    private WindowManager mWindowManager ;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;

    private int resultCode;
    private Intent resultData;

    private Handler handler = new Handler();
    private int  clickNum = 0;//点击次数用来判断是否双击
    public  MyApplication application;

    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationChannel notificationChannel;
    private final static String NOTIFICATION_CHANNEL_ID = "CHANNEL_ID";
    private final static String NOTIFICATION_CHANNEL_NAME = "CHANNEL_NAME";
    private final static int FOREGROUND_ID=1;



    public FloatBallService() {

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 初始化 WindowManager和 LayoutInflater
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        mLayoutInflater = LayoutInflater.from(this);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        application = (MyApplication) getApplication();
        mediaProjectionManager = application.getMediaProjectionManager();
        creatView();
        setView();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatBall != null){
            mWindowManager.removeView(mFloatBall);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        resultCode = intent.getIntExtra("code",-1);
        resultData = intent.getParcelableExtra("data");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,resultData);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        intent = new Intent(getApplicationContext(), MainActivity.class);  //点击通知栏后想要被打开的页面MainActivity.class
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);  //点击通知栏触发跳转
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.screen)
                    .setContentTitle("截图宝")
                    .setContentText("点击进入截图宝主页")
                    .setContentIntent(pendingIntent)
                    .build();

            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(FOREGROUND_ID, notification);
        }
        return Service.START_STICKY;
    }

    /**
     * 生成漂浮球
     */
    private void creatView() {
        mFloatBall = mLayoutInflater.inflate(R.layout.sy_floating_view,null);
        mFloatBall.setOnTouchListener(new FloatViewTouchListner());
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.LEFT |Gravity.TOP;
        //设置window type

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type =WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //注意该属性的设置很重要，FLAG_NOT_FOCUSABLE使浮动窗口不获取焦点,若不设置该属性，屏幕的其它位置点击无效，应为它们无法获取焦点
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置视图的显示位置，通过WindowManager更新视图的位置其实就是改变(x,y)的值
        mCurrentX = mLayoutParams.x = 50;
        mCurrentY = mLayoutParams.y = 50;
        //设置视图的宽和高
        mLayoutParams.width = 100;
        mLayoutParams.height = 100;
        //将视图添加到windwo中
        mWindowManager.addView(mFloatBall,mLayoutParams);
    }

    /**
     * 设置点击事件
     */
    private void setView(){
        Intent intent = new Intent(FloatBallService.this,MainActivity.class);
        mFloatBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PosteTask.IP = application.getIP();
                if (PosteTask.IP == null){
                    Handler handlerThree=new Handler(Looper.getMainLooper());
                    handlerThree.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getApplicationContext() ,"请输入IP地址",Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                mFloatBall.setVisibility(View.INVISIBLE);
                ScreenShot.getWH(mWindowManager);
                ScreenShot.createImageReader();
                ScreenShot.beginScreenShot(mediaProjection,FloatBallService.this,application.getIfSaveImage());
                mFloatBall.setVisibility(View.VISIBLE);

                if (ScreenShot.ifConfirmPoste){
                    //弹出Toast
                    Handler handlerThree=new Handler(Looper.getMainLooper());
                    handlerThree.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getApplicationContext() ,"截图成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    /*该方法用来更新视图的位置，其实就是改变(LayoutParams.x,LayoutParams.y)的值*/
    private void updateFloatView() {
        mLayoutParams.x = mCurrentX;
        mLayoutParams.y = mCurrentY;
        mWindowManager.updateViewLayout(mFloatBall, mLayoutParams);
    }

    /*处理视图的拖动，这里只对Move事件做了处理，用户也可以对点击事件做处理，例如：点击浮动窗口时，启动应用的主Activity*/
    private class FloatViewTouchListner implements View.OnTouchListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /**
             * getRawX(),getRawY()这两个方法很重要。通常情况下，我们使用的是getX(),getY()来获得事件的触发点坐标，
             * 但getX(),getY()获得的是事件触发点相对与视图左上角的坐标；而getRawX(),getRawY()获得的是事件触发点
             * 相对与屏幕左上角的坐标。由于LayoutParams中的x,y是相对与屏幕的，所以需要使用getRawX(),getRawY()。
             */
            mCurrentX = (int) event.getRawX() - mFloatViewWidth;
            mCurrentY = (int) event.getRawY() - mFloatViewHeight;
            int action = event.getAction();
            switch (action) {
                //按一下就截图
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateFloatView();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;//returnfalse时会执行onclick
        }
    }
}