package com.mrducan.floatscreenball;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class PosteTask {

    public static String IP = null;//服务端的IP地址
    private static final int PORT = 8009;
    private static OutputStream out ;


    //先把文件名发出去，让服务器创建同名文件
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void posteFileName(String fileName, Context context){
        try {
            Socket socket = new Socket(IP,PORT);
            if (socket == null){
                showToast(context,"未与目标电脑建立通信，请确保输入IP正确、网络正常");
                return;
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(getHostIP());
            bufferedWriter.newLine();
            bufferedWriter.write(fileName);
            bufferedWriter.flush();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //全局弹出Toast
    private static void showToast(Context context,String text) {
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){
            public void run(){
                Toast.makeText(context.getApplicationContext() ,text,Toast.LENGTH_LONG).show();
            }
        });
    }


    //    发送图片到客户端
    public static void postePic(Bitmap bitmap) {
        try {
            Socket socket = new Socket(IP,PORT);
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


    public static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }

}
