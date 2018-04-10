package com.example.dachui.socketdemo;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.dachui.socketdemo.receiver.WifiAPBroadcastReceiver;
import com.example.dachui.socketdemo.utils.ApMgr;
import com.example.dachui.socketdemo.utils.NetUtils;
import com.example.dachui.socketdemo.utils.WifiMgr;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * ap模式进行数据传输
 * 服务器端的手机发出热点
 * 客户端手机连接该热点进行通信
 * 参考
 *      https://www.jianshu.com/p/1b0b337829f5
 */
public class ApActivity extends AppCompatActivity {
    public static String TAG = ApActivity.class.getSimpleName();
    public static int PORT = 8686;

    /**
     * 给热点定个名字
     */
    private String mSsid = "liudachuia";
    private TextView mDescTv;
    private WifiAPBroadcastReceiver mWifiAPBroadcastReceiver;
    private boolean mIsInitialized = false;
    private Runnable mUdpServerRuannable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap);

        mDescTv = (TextView) findViewById(R.id.desc_tv);
    }

    /***
     * 做为服务器端
     * @param v
     */
    public void server(View v) {
        mDescTv.setText("初始化中...");
        //1.初始化热点
        WifiMgr.getInstance(this).disableWifi();
        if (ApMgr.isApOn(this)) {
            ApMgr.disableAp(this);
        }
        mWifiAPBroadcastReceiver = new WifiAPBroadcastReceiver() {
            @Override
            public void onWifiApEnabled() {
                Log.i(TAG, "======>>>onWifiApEnabled !!!");
                if (!mIsInitialized) {
                    /**
                     * 开启socket监听客户端的连接
                     */
                    mUdpServerRuannable = createSendMsgToFileSenderRunnable();
                    new Thread(mUdpServerRuannable).start();
                    mIsInitialized = true;
                    mDescTv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDescTv.setText("初始化成功，等待连接...");
                        }
                    }, 2 * 1000);
                }
            }
        };
        IntentFilter filter = new IntentFilter(WifiAPBroadcastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mWifiAPBroadcastReceiver, filter);
        ApMgr.isApOn(this); // check Ap state :boolean
        ApMgr.configApState(this, mSsid); // change Ap state :boolean
    }

    //======================================服务器端代码=====================================================
    //======================================服务器端代码=====================================================
    //======================================服务器端代码=====================================================

    /**
     * 创建发送UDP消息到客户端的服务线程
     */
    private Runnable createSendMsgToFileSenderRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startFileReceiverServer(PORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * 开启 服务器端通信服务 (必须在子线程执行)
     *
     * @param serverPort
     * @throws Exception
     */
    DatagramSocket mDatagramSocket;

    private void startFileReceiverServer(int serverPort) throws Exception {

        //网络连接上，无法获取IP的问题
        int count = 0;
        String localAddress = WifiMgr.getInstance(this).getHotspotLocalIpAddress();
        while (localAddress.equals("0.0.0.0") && count < 20) {
            Thread.sleep(1000);
            localAddress = WifiMgr.getInstance(this).getHotspotLocalIpAddress();
            Log.i(TAG, "receiver get local Ip ----->>>" + localAddress);
            count++;
        }
        Log.i(TAG, "receiver get local Ip ----->>>" + localAddress);
        mDatagramSocket = new DatagramSocket(serverPort);
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        while (true) {
            //1.接收 文件发送方的消息
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            mDatagramSocket.receive(receivePacket);
            String msg = new String(receivePacket.getData()).trim();
            InetAddress inetAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            if (msg != null) {
                Log.i(TAG, "Get the msg from FileReceiver######>ip>>" + inetAddress);
                Log.i(TAG, "Get the msg from FileReceiver######>>port>" + port);
                Log.i(TAG, "Get the msg from FileReceiver######>>msg>" + msg);

            }

            //2.反馈 给客户端发消息
            sendData = "I am server!~~~~".getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, port);
            mDatagramSocket.send(sendPacket);
        }
    }

    //======================================客户端端代码=====================================================
    //======================================客户端端代码=====================================================
    //======================================客户端端代码=====================================================

    /**
     * 做为客户端
     *
     * @param v
     */
    public void client(View v) {
        if (!WifiMgr.getInstance(this).isWifiEnable()) {//wifi未打开的情况
            WifiMgr.getInstance(this).openWifi();
        }

        //1.连接网络
        WifiMgr.getInstance(this).openWifi();
        WifiMgr.getInstance(this).addNetwork(WifiMgr.createWifiCfg(mSsid, null, WifiMgr.WIFICIPHER_NOPASS));

        //2.发送UDP通知信息到 文件接收方 开启ServerSocketRunnable
        mUdpServerRuannable = createSendMsgToServerRunnable(WifiMgr.getInstance(this).getIpAddressFromHotspot());
        new Thread(mUdpServerRuannable).start();
    }

    /**
     * 创建发送UDP消息到服务器
     *
     * @param serverIP
     */
    private Runnable createSendMsgToServerRunnable(final String serverIP) {
        Log.i(TAG, "receiver serverIp ----->>>" + serverIP);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startFileSenderServer(serverIP, PORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * 客户端发送消息给服务器 并监听服务器消息
     *
     * @param targetIpAddr
     * @param serverPort
     * @throws Exception
     */
    private void startFileSenderServer(String targetIpAddr, int serverPort) throws Exception {
//        Thread.sleep(3*1000);
        // 确保Wifi连接上之后获取得到IP地址
        int count = 0;
        while (targetIpAddr.equals("0.0.0.0") && count < 20) {
            Thread.sleep(1000);
            targetIpAddr = WifiMgr.getInstance(this).getIpAddressFromHotspot();
            Log.i(TAG, "receiver serverIp ----->>>" + targetIpAddr);
            count++;
        }
        Log.i(TAG, "try to ping ----->>>" + targetIpAddr);
        // 即使获取到连接的热点wifi的IP地址也是无法连接网络 所以采取此策略
        count = 0;
        while (!NetUtils.pingIpAddress(targetIpAddr) && count < 20) {
            Thread.sleep(500);
            Log.i(TAG, "try to ping ----->>>" + targetIpAddr + " - " + count);
            count++;
        }

        mDatagramSocket = new DatagramSocket(serverPort);
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        InetAddress ipAddress = InetAddress.getByName(targetIpAddr);


        //1.发送给服务器端
        sendData = "hello,I am client!!!!".getBytes("UTF-8");
        DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length, ipAddress, serverPort);

        //2.接收服务器端消息
        int times = 50;
        while (times > 0) {
            times--;
            mDatagramSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            mDatagramSocket.receive(receivePacket);
            String response = new String(receivePacket.getData(), "UTF-8").trim();
            InetAddress address = receivePacket.getAddress();
            int port = receivePacket.getPort();
            if (response != null) {
                // 获取服务器消息
                final String str = address + "--" + port + "--" + response;
                Log.d(TAG, str);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDescTv.setText(str);
                    }
                });
            }
        }
    }
}
