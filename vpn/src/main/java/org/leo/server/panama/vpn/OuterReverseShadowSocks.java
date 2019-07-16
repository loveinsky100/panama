package org.leo.server.panama.vpn;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.handler.Redirect2ReverseShadowSocksRequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 部署在外网服务器，配合InnerReverseShadowSocks使用
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class OuterReverseShadowSocks {
    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration shadowSocksConfiguration = new ShadowSocksConfiguration();

        // 本地配置
        shadowSocksConfiguration.setType("aes-256-cfb");
        shadowSocksConfiguration.setPassword("1234567890");

        // 反向代理服务配置
        shadowSocksConfiguration.setReverse(true);
        shadowSocksConfiguration.setReversePort(8786);

        // 代理内网端配置
        shadowSocksConfiguration.setProxyType("aes-256-cfb");
        shadowSocksConfiguration.setProxyPassword("123456789");

        Server server = new TCPServer(9898, new Redirect2ReverseShadowSocksRequestHandler(shadowSocksConfiguration));

        System.out.println(server.getClass().getSimpleName() + " start");
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    private static String read(String message) throws IOException {
        System.out.print(message + ":");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }
}
