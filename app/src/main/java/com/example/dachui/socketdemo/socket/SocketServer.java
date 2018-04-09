package com.example.dachui.socketdemo.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>描述：socket服务器端</p>
 * 使用的是tcp
 * <p>
 * 一个基本的客户端/服务器端程序代码。主要实现了服务器端一直监听某个端口，等待客户端连接请求。
 * 客户端根据IP地址和端口号连接服务器端，从键盘上输入一行信息，发送到服务器端，
 * 然后接收服务器端返回的信息，最后结束会话。
 * 这个程序一次只能接受一个客户连接。
 * 作者： liujiyi<br>
 * 日期： 2018/4/9<br>
 */
public class SocketServer implements Runnable {
    public static String TAG = SocketServer.class.getSimpleName();

    @Override
    public void run() {
        startServer();
    }

    private void startServer() {
        try {
            // 创建一个ServerSocket在端口2013监听客户请求
            ServerSocket serverSocket = new ServerSocket(2018);
            while (true) {
                // 侦听并接受到此Socket的连接,请求到来则产生一个Socket对象，并继续执行
                Socket socket = serverSocket.accept();

                /**
                 * 注：--------------------------
                 * 以下代码只能用于一个客户端连接该服务器
                 * 如果要实现多个客户端连接服务器，启一个线程进行处理则可
                 */

                //获取连接到该服务器端的设备的ip地址
                InetAddress inetAddress = socket.getInetAddress();
                Log.d(TAG, "==========================================================");
                Log.d(TAG, inetAddress.toString());
                Log.d(TAG, inetAddress.getHostAddress());

                // 由Socket对象得到输入流，并构造相应的BufferedReader对象
                // 由Socket对象得到输出流，并构造PrintWriter对象
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

                String result = "";
                while (result != null && result.indexOf("bye") == -1) {
                    //readLine()是一个阻塞函数，当没有数据读取时，就一直会阻塞在那，而不是返回null
                    //如果不指定buffer大小，则readLine()使用的buffer有8192个字符。在达到buffer大小之前，只有遇到"/r"、"/n"、"/r/n"才会返回。
                    printWriter.print("hello Client, I am Server! \r\n");
                    printWriter.flush();

                    // 获取从客户端读入的字符串
                    result = bufferedReader.readLine();
                    Log.d(TAG, "Client say : " + result);
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
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e);
        }
    }
}
