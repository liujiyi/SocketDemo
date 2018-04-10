package com.example.dachui.socketdemo.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * <p>描述：客户端socket</p>
 * 使用的是tcp
 * 作者： liujiyi<br>
 * 日期： 2018/4/9<br>
 */
public class SocketUdpClient implements Runnable {
    public static String TAG = SocketUdpClient.class.getSimpleName();

    /**
     * 注：
     * 192.168.1.101为测试使用的服务器端手机HTC ip地址
     */
//    private String netAddress = "192.168.1.101";
    private String netAddress = "10.8.3.119";
    private final int PORT = 2018;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    @Override
    public void run() {
        startClient();
    }

    private void startClient() {
        try {
            /*** 发送数据***/
            // 初始化datagramSocket,注意与前面Server端实现的差别
            datagramSocket = new DatagramSocket();
            // 使用DatagramPacket(byte buf[], int length, InetAddress address, int port)函数组装发送UDP数据报
            String sendStr = "I am client!!!";
            byte[] buf = sendStr.getBytes();
            InetAddress address = InetAddress.getByName(netAddress);
            datagramPacket = new DatagramPacket(buf, buf.length, address, PORT);

            /*** 接收数据***/
            byte[] receBuf = new byte[1024];
            DatagramPacket recePacket = new DatagramPacket(receBuf, receBuf.length);

            int count = 50;
            while (count > 0) {
                count--;
                // 发送数据
                datagramSocket.send(datagramPacket);

                datagramSocket.receive(recePacket);

                String receStr = new String(recePacket.getData(), 0, recePacket.getLength());
                Log.d(TAG, "Server msg:" + receStr);
                Log.d(TAG, recePacket.getPort() + "");
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e);
        } finally {
            // 关闭socket
            Log.d(TAG, "==close==");
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }
}
