package com.mrducan.floatscreenball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.Image.Plane;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveTask extends AsyncTask<Image, Void, Bitmap> {
    public String fileURL;
    public String fileName;
    public int TAG = 0;//用这个标签确定是否执行过发送图片task
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public boolean ifSaveImage;//是否保存截图为本地文件

    public SaveTask(Context context,Boolean ifSaveImage){
        super();
        this.context = context;
        this.ifSaveImage = ifSaveImage;

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Bitmap doInBackground(Image... args) {
        if (null == args || 1 > args.length || null == args[0]) {
            return null;
        }

        Image image = args[0];

        int width;
        int height;
        try {
            width = image.getWidth();
            height = image.getHeight();
        } catch (IllegalStateException e) {
            return null;
        }


        final Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();

        // 每个像素的间距
        int pixelStride = planes[0].getPixelStride();
        // 总的间距
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        File fileImage = null;

        if (null != bitmap) {
            fileURL = createFile();
            if (ifSaveImage){
                FileOutputStream fos = null;
                try {
                    fileImage = new File(fileURL);
                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                        fos = new FileOutputStream(fileImage);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                    }
                } catch (IOException e) {
                    fileImage = null;
                } finally {
                    if (null != fos) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }

                }



            }

            //开启新的线程发送图片
            Bitmap finalBitmap = bitmap;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PosteTask.posteFileName(fileName,context);
                    PosteTask.postePic(finalBitmap);
                    TAG = 1;
                }
            }).start();

            if (null != bitmap && !bitmap.isRecycled() && TAG == 1) {
                bitmap.recycle();
                bitmap = null;
            }

            if (null != fileImage || TAG == 1) {
                return bitmap;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (ScreenShot.surface.isValid()) {
            ScreenShot.surface.release();
        }
    }

    // 输出目录
    public String createFile() {
        String outDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String date = simpleDateFormat.format(new Date());
        fileName = date + ".png";
        return outDir + fileName;
    }

}
