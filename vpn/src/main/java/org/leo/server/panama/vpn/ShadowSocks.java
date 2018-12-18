package org.leo.server.panama.vpn;

import org.apache.log4j.Logger;
import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.handler.AgentShadowSocksRequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class ShadowSocks {
    public static void main(String []args) throws IOException {

        ShadowSocksConfiguration.setType("aes-256-cfb");
        ShadowSocksConfiguration.setPassword("1234567890");

//        ShadowSocksConfiguration.setProxy("your proxy ip");
//        ShadowSocksConfiguration.setProxyType("your proxy type like aes-256-cfb");
//        ShadowSocksConfiguration.setProxyPwd("you proxy password");
//        ShadowSocksConfiguration.setProxyPort(9898); // your proxy port

        Server server = new TCPServer(9898, new AgentShadowSocksRequestHandler());
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    private static String read(String message) throws IOException {
        System.out.print(message + ":");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }
}
