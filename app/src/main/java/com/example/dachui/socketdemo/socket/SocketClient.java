package com.example.dachui.socketdemo.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <p>描述：客户端socket</p>
 * 使用的是tcp
 * 作者： liujiyi<br>
 * 日期： 2018/4/9<br>
 */
public class SocketClient implements Runnable {
    public static String TAG = SocketClient.class.getSimpleName();

    @Override
    public void run() {
        startClient();
    }

    private void startClient() {
        try {
            /**
             * 注：
             * 192.168.1.101为测试使用的服务器端手机HTC ip地址
             */
            Socket socket = new Socket("192.168.1.101", 2018);
            socket.setSoTimeout(60000);

            // 由Socket对象得到输入流，并构造相应的BufferedReader对象
            // 由Socket对象得到输出流，并构造PrintWriter对象
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String result = "";
            int count = 0;
            while (result != null && count < 50 && result.indexOf("bye") == -1) {
                count++;
                //readLine()是一个阻塞函数，当没有数据读取时，就一直会阻塞在那，而不是返回null
                //如果不指定buffer大小，则readLine()使用的buffer有8192个字符。在达到buffer大小之前，只有遇到"/r"、"/n"、"/r/n"才会返回。
                result = bufferedReader.readLine();

                printWriter.print("hello Server, I am Client!" + count + "\r\n");
                printWriter.flush();
                Log.d(TAG, "====Server say : " + result + "====");
            }

            Log.d(TAG, "==close==");
            /** 关闭Socket*/
            if (printWriter != null) {
                printWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e);
        } finally {

        }
    }
}
