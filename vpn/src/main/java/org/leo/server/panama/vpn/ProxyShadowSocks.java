package org.leo.server.panama.vpn;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.handler.AgentShadowSocksRequestHandler;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 普通代理ss，测试方式，启动ProxyShadowSocks2，然后启动ProxyShadowSocks，客户端连接9898端口
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class ProxyShadowSocks {
    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration.setType("aes-256-cfb");
        ShadowSocksConfiguration.setPassword("1234567890");

        // 35.229.192.233请勿滥用
        ShadowSocksConfiguration.setProxy("127.0.0.1");
        ShadowSocksConfiguration.setProxyType("aes-256-cfb");
        ShadowSocksConfiguration.setProxyPassword("1234567890");
        ShadowSocksConfiguration.setProxyPort(9899); // your proxy port

        Server server = new TCPServer(9898, new AgentShadowSocksRequestHandler());
        ShadowSocksProxyFactory.startReverseServer();

        System.out.println(server.getClass().getSimpleName() + " start");
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    private static String read(String message) throws IOException {
        System.out.print(message + ":");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }
}
