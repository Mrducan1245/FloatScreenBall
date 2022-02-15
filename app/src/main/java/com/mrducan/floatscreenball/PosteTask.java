package com.mrducan.floatscreenball;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PosteTask {

    private static Socket socket;
    public static String IP = null;//服务端的IP地址
    private static final int PORT = 8009;

    private static FileInputStream fis;

    private static OutputStream out ;

    //先把文件名发出去，让服务器创建同名文件
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void posteFileName(String fileName){

        System.out.println("发送的文件名是："+fileName);
        Log.e("PosteTASK","发送的图片名字为："+fileName);
        try {
            byte[] data = fileName.getBytes(StandardCharsets.UTF_8);
            DatagramPacket dp = new DatagramPacket(data,data.length, InetAddress.getByName(IP),9000);
            DatagramSocket ds = new DatagramSocket();
            ds.send(dp);
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    发送图片到客户端
    public static void postePic(Bitmap bitmap) {
        try {
//            fis = new FileInputStream(fileURL);
            socket = new Socket(IP,PORT);
            out = socket.getOutputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,baos);
            byte[] data = baos.toByteArray();
            out.write(data);
            //关闭流和套接字
            out.flush();
            socket.shutdownOutput();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
