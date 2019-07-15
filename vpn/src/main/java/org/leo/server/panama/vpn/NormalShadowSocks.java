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
 * 普通的ss，直接启动即可
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class NormalShadowSocks {
    public static void main(String []args) throws IOException {

        ShadowSocksConfiguration.setType("aes-256-cfb");
        ShadowSocksConfiguration.setPassword("1234567890");

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
