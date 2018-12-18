package org.leo.server.panama.vpn.reverse;

import java.util.function.Consumer;

/**
 * 反向代理客户端，发送请求到代理端，代理端返回数据
 * 服务端向被代理端进行长链接，服务端维持和代理端的连接
 * 代理端收到客户端请求，通过长链接发送请求到服务端
 * 服务端转发请求到服务端的ss端口，请求返回后返回给代理端
 * @author xuyangze
 * @date 2018/11/21 3:17 PM
 */
public interface ReverseClient {
    /**
     * 开启
     * @param port 端口
     * @param maxThread 最大线程数
     */
    void start(int port, int maxThread);

    /**
     * 关闭
     */
    void close();

    /**
     * 发送给反向代理
     * @param data
     * @param callback
     */
    void send(byte[] data, Consumer<byte []> callback);
}
