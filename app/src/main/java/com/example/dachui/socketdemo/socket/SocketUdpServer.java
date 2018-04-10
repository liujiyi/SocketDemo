package com.example.dachui.socketdemo.socket;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * <p>描述：socket服务器端</p>
 * 使用的是udp
 * <p>
 * 作者： liujiyi<br>
 * 日期： 2018/4/9<br>
 */
public class SocketUdpServer implements Runnable {
    public static String TAG = SocketUdpServer.class.getSimpleName();

    // 定义一些常量
    private final int MAX_LENGTH = 1024; // 最大接收字节长度
    private final int PORT = 2018;   // port号
    // 用以存放接收数据的字节数组
    private byte[] receMsgs = new byte[MAX_LENGTH];
    // 数据报套接字
    private DatagramSocket datagramSocket;
    // 用以接收数据报
    private DatagramPacket datagramPacket;

    @Override
    public void run() {
        startServer();
    }

    private void startServer() {
        try {
            // 创建一个数据报套接字，并将其绑定到指定port上
            datagramSocket = new DatagramSocket(PORT);
            // DatagramPacket(byte buf[], int length),建立一个字节数组来接收UDP包
            datagramPacket = new DatagramPacket(receMsgs, receMsgs.length);

            while (true) {
                /**
                 * 如果需要多客户端连接  在这里新建一个线程管理监听到的每一个客户端连接！！！
                 *
                 */
                // receive()来等待接收UDP数据报 阻塞操作
                datagramSocket.receive(datagramPacket);

                /****** 解析数据报****/
                String receStr = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                Log.d(TAG, "Client ip:" + datagramPacket.getAddress());
                Log.d(TAG, "Client Port:" + datagramPacket.getPort());
                Log.d(TAG, "Client msg:" + receStr);

                /***** 返回ACK消息数据报*/
                // 组装数据报
                byte[] buf = "I receive the message".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, datagramPacket.getAddress(), datagramPacket.getPort());
                // 发送消息
                datagramSocket.send(sendPacket);
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e);
        } finally {
            Log.d(TAG, "==close==");
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }
}
