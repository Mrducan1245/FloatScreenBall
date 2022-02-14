package com.mrducan.floatscreenball;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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
    private static final String IP ="192.168.43.17";//服务端的IP地址
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
    public static void postePic(String fileURL) {
        try {
            fis = new FileInputStream(fileURL);
            byte[] bytes = new byte[1024];
            //得到图片大小
            socket = new Socket(IP,PORT);
            out = socket.getOutputStream();
            System.out.println("socket创建成功");

            //读取服务器响应数据
//            byte[] getRec = new byte[1];
//            InputStream is = socket.getInputStream();
//            is.read(getRec);
//            String message = new String(getRec);
//
//            //如果得到的反馈是1，说明服务端已经接收完成图片大小，可以开始发送图片了
//            if (message.equals("1")){
//                out.write(bytes,0,length);
//                out.flush();
//            }
            while (fis.read(bytes) != -1){
                out.write(bytes);
                out.flush();
            }
            //关闭流和套接字
            socket.shutdownOutput();
            fis.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
