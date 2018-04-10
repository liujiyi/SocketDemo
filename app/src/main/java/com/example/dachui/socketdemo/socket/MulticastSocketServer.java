package com.example.dachui.socketdemo.socket;

import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * <p>描述：multicastsocket服务器端</p>
 * <p>
 *     注：手机必须在同一个局域网内，否则接收不到数据！！
 * <p>
 * 发送方发送广播
 * 作者： liujiyi<br>
 * 日期： 2018/4/9<br>
 */
public class MulticastSocketServer implements Runnable {
    public static String TAG = MulticastSocketServer.class.getSimpleName();

    public static int PORT = 6666;
    public static String BROADCAST_IP = "224.0.0.1";

    /**
     * 注：
     * 192.168.1.101为测试使用的服务器端手机HTC ip地址
     */
//    private String netAddress = "192.168.1.101";
    private String netAddress = "10.8.3.119";

    @Override
    public void run() {
        startServer();
    }

    private void startServer() {
        try {
            //1.创建一个multicastsocket对象
            MulticastSocket multicastSocket = new MulticastSocket(PORT);

            //2.创建一个 InetAddress .要使用多点广播,需要让一个数据报标有一组目标主机地址,
            // 其思想便是设置一组特殊网络地址作为多点广播地址,第一个多点广播地址都被看作是一个组,
            // 当客户端需要发送.接收广播信息时,加入该组就可以了.IP协议为多点广播提供这批特殊的IP地址,
            // 这些IP地址范围是224.0.0.0---239.255.255.255,其中224.0.0.0为系统自用.下面
            // BROADCAST_IP是自己声明的一个String类型的变量,其范围但是前面所说的IP范围,比如
            // BROADCAST_IP="224.0.0.1"
            //224.0.0.1为广播地址
            InetAddress address = InetAddress.getByName(BROADCAST_IP);
            //这个地方可以输出判断该地址是不是广播类型的地址
            Log.d(TAG, address.isMulticastAddress() + "");

            //4.将要广播的数据转为byte类型
            byte[] data = netAddress.getBytes();

            //5. 创建一个DatagramPacket 对象，并指定要讲这个数据包发送到网络当中的哪个地址，以及端口号
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, PORT);

            //6. 将该MulticastSocket对象加入到指定的多点广播地址
            // ,MulticastSocket使用public void joinGroup(InetAddress mcastaddr)
            // throws IOException方法加入指定组.
            multicastSocket.joinGroup(address);

            //创建一个byte数组用于接收
            byte[] data2 = new byte[1024];
            //创建一个空的DatagramPackage对象
            DatagramPacket packet = new DatagramPacket(data2, data2.length);

            int count = 20;
            //7发送
            while (true) {
                count--;
                multicastSocket.send(datagramPacket);
                SystemClock.sleep(1000);

                if (count < 0) {
                    multicastSocket.receive(packet);
                    /****** 解析数据报****/
                    String receStr = new String(packet.getData(), 0, packet.getLength());
                    Log.d(TAG, "Client ip:" + packet.getSocketAddress());
                    Log.d(TAG, "Client ip:" + packet.getAddress());
                    Log.d(TAG, "Client Port:" + packet.getPort());
                    Log.d(TAG, "Client msg:" + receStr);
                }
                Log.d(TAG, "server send====" + netAddress);
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e);
        }
    }
}
