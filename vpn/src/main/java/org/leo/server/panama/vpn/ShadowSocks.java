package org.leo.server.panama.vpn;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.handler.AgentShadowSocksRequestHandler;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;
import org.leo.server.panama.vpn.reverse.server.ReverseShadowSocksServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 部署在外网服务器
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class ShadowSocks {
    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration.setType("aes-256-cfb");
        ShadowSocksConfiguration.setPassword("1234567890");
//        ShadowSocksConfiguration.setProxy("35.229.192.233");
//        ShadowSocksConfiguration.setProxyType("aes-256-cfb");
//        ShadowSocksConfiguration.setProxyPassword("123456");
//        ShadowSocksConfiguration.setProxyPort(9898);
        ShadowSocksConfiguration.setReverse(true);

//        ShadowSocksConfiguration.setReverseHost("127.0.0.1");
//        ShadowSocksConfiguration.setReversePort(8080);

        Server server;
        if (null != ShadowSocksConfiguration.getReverseHost()) {
            server = new ReverseShadowSocksServer(ShadowSocksConfiguration.getReverseHost(), ShadowSocksConfiguration.getReversePort());
        } else {
            server = new TCPServer(9898, new AgentShadowSocksRequestHandler());
        }

        if (ShadowSocksConfiguration.isReverse()) {
            ShadowSocksProxyFactory.start();
        }

        System.out.println(server.getClass().getSimpleName() + " start");
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    private static String read(String message) throws IOException {
        System.out.print(message + ":");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }
}
