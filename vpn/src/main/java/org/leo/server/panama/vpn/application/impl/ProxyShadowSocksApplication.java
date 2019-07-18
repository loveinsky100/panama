package org.leo.server.panama.vpn.application.impl;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.vpn.application.ShadowSocksApplication;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.handler.AgentShadowSocksRequestHandler;

import java.io.IOException;

/**
 * 普通代理ss，测试方式，启动ProxyShadowSocks2，然后启动ProxyShadowSocks，客户端连接9898端口
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class ProxyShadowSocksApplication implements ShadowSocksApplication {
    @Override
    public void start(ShadowSocksConfiguration shadowSocksConfiguration) {
        Server server = new TCPServer(shadowSocksConfiguration.getPort(), new AgentShadowSocksRequestHandler(shadowSocksConfiguration));
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration shadowSocksConfiguration = new ShadowSocksConfiguration();

        shadowSocksConfiguration.setType("aes-256-cfb");
        shadowSocksConfiguration.setPassword("1234567890");
        shadowSocksConfiguration.setPort(9898);

        // 代理服务配置
        shadowSocksConfiguration.setProxy("127.0.0.1");
        shadowSocksConfiguration.setProxyType("aes-256-cfb");
        shadowSocksConfiguration.setProxyPassword("123456789");
        shadowSocksConfiguration.setProxyPort(9899); // your proxy port

        ShadowSocksApplication shadowSocksApplication = new ProxyShadowSocksApplication();
        shadowSocksApplication.start(shadowSocksConfiguration);
    }
}
