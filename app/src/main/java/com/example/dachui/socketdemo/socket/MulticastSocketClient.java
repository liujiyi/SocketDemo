package com.example.dachui.socketdemo.socket;

import android.os.SystemClock;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * <p>描述：multicastsocket客户端</p>
 * <p>
 * <p>
 * 客户端接收广播
 * 作者： liujiyi<br>
 * 日期： 2018/4/9<br>
 */
public class MulticastSocketClient implements Runnable {
    public static String TAG = MulticastSocketClient.class.getSimpleName();

    public static int PORT = 6666;
    public static String BROADCAST_IP = "224.0.0.1";

    /**
     * 注：
     * 192.168.1.101为测试使用的服务器端手机HTC ip地址
     */
//    private String netAddress = "192.168.1.101";
//    private String netAddress = "10.8.3.119";

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

            //3.将该MulticastSocket对象加入到指定的多点广播地址
            // ,MulticastSocket使用public void joinGroup(InetAddress mcastaddr)
            // throws IOException方法加入指定组.
            multicastSocket.joinGroup(address);

            //4创建一个byte数组用于接收
            byte[] data = new byte[1024];

            //5.创建一个空的DatagramPackage对象
            DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);

            int count = 50;
            while (count > 0) {
                //6.使用receive方法接收发送方所发送的数据,同时这也是一个阻塞的方法
                multicastSocket.receive(packet);

                //7.得到发送过来的数据
                String serverIP = new String(packet.getData(), packet.getOffset(), packet.getLength());
                Log.d(TAG, "get ip :==="+serverIP);

                //获取到ip后发送数据给服务器
                /*** 发送数据***/
                // 初始化datagramSocket,注意与前面Server端实现的差别
                DatagramSocket datagramSocket = new DatagramSocket();
                // 使用DatagramPacket(byte buf[], int length, InetAddress address, int port)函数组装发送UDP数据报
                String sendStr = "I am client!!!";
                byte[] buf = sendStr.getBytes();
                InetAddress address2 = InetAddress.getByName(serverIP);
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, address2, PORT);
                datagramSocket.send(datagramPacket);

                count--;
//                SystemClock.sleep(1000);
            }

            //8.如果不想再接收,广播数据,可以采用 public void leaveGroup(InetAddress mcastaddr) throws IOException
//            multicastSocket.leaveGroup(address);
        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e);
        }
    }
}
