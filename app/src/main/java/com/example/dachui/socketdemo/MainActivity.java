package com.example.dachui.socketdemo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.dachui.socketdemo.socket.SocketClient;
import com.example.dachui.socketdemo.socket.SocketServer;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIp();
    }

    /**
     * 获取手机的ip地址
     */
    private void getIp() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        Log.d(TAG, ip);
        //192.168.1.101  HTC
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    /***
     * 做为服务器端
     * @param v
     */
    public void server(View v) {
        SocketServer server = new SocketServer();
        new Thread(server).start();
    }

    /**
     * 做为客户端
     * @param v
     */
    public void client(View v) {
        SocketClient server = new SocketClient();
        new Thread(server).start();
    }
}
