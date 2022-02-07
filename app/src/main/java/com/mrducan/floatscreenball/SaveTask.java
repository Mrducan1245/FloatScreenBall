package com.mrducan.floatscreenball;

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
            FileOutputStream fos = null;
            try {
                fileURL = createFile();
                fileImage = new File(fileURL);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                    fos = new FileOutputStream(fileImage);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();

                    //发送图片到客户端

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
                if (null != bitmap && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }

        if (null != fileImage) {
            return bitmap;
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
        return outDir + date + ".png";
    }

}
