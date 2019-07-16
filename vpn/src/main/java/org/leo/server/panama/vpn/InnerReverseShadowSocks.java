package org.leo.server.panama.vpn;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.reverse.server.ReverseShadowSocksServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 反向代理ss，部署在内网服务器，配合OuterReverseShadowSocks使用
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class InnerReverseShadowSocks {
    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration shadowSocksConfiguration = new ShadowSocksConfiguration();
        shadowSocksConfiguration.setType("aes-256-cfb");
        shadowSocksConfiguration.setPassword("123456789");
//        shadowSocksConfiguration.setReverseHost("35.229.192.233");
        shadowSocksConfiguration.setReverseHost("127.0.0.1");
        shadowSocksConfiguration.setReversePort(8786);

        Server server = new ReverseShadowSocksServer(shadowSocksConfiguration);
        System.out.println(server.getClass().getSimpleName() + " start");
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    private static String read(String message) throws IOException {
        System.out.print(message + ":");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }
}
