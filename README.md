socket编程
    基于TCP协议
    （使用测试机htc做为服务器端）
 * 一个基本的客户端/服务器端程序代码。主要实现了服务器端一直监听某个端口，等待客户端连接请求。
 * 客户端根据IP地址和端口号连接服务器端，连接成功后，发送消息到服务器端，并监听服务器端返回的消息。
 * 这个程序一次只能接受一个客户连接。（多客户端的话需开启线程进行管理）
 详见代码
    SocketServer
    SocketClient

    基于UDP协议
 详见代码
    SocketUdpServer
    SocketUdpClient
