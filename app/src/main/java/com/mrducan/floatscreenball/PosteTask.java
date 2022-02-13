package com.mrducan.floatscreenball;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class PosteTask {

    private static Socket socket;
    private static final String IP ="192.168.10.61";
    private static final int PORT = 8009;

    private static FileInputStream fis;

    private static OutputStream out ;


//    发送图片到客户端
    public static void postePic(String fileURL) {
        try {
            fis = new FileInputStream(fileURL);
            byte[] bytes = new byte[1024*1024];
            //得到图片大小
            int length = fis.read(bytes);
            String lenString = String.valueOf(bytes);
            Log.e("我的测试","得到图片大小"+length);

            socket = new Socket(IP,PORT);
            out = socket.getOutputStream();
            Log.e("我的测试","创建输出流成功");

            //向客户端发送图片大小
            out.write(lenString.getBytes());
            out.flush();

            //读取服务器响应数据
            byte[] getRec = new byte[1];
            InputStream is = socket.getInputStream();
            is.read(getRec);
            String message = new String(getRec);

            //如果得到的反馈是1，说明服务端已经接收完成图片大小，可以开始发送图片了
            if (message.equals("1")){
                out.write(bytes,0,length);
                out.flush();
                Log.e("我的测试","图片发送成功");
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
