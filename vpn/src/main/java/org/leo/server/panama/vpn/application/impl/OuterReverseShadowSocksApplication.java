package org.leo.server.panama.vpn.application.impl;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.vpn.application.ShadowSocksApplication;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.handler.Redirect2ReverseShadowSocksRequestHandler;

import java.io.IOException;

/**
 * 部署在外网服务器，配合InnerReverseShadowSocks使用
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class OuterReverseShadowSocksApplication implements ShadowSocksApplication {
    @Override
    public void start(ShadowSocksConfiguration shadowSocksConfiguration) {
        Server server = new TCPServer(shadowSocksConfiguration.getPort(), new Redirect2ReverseShadowSocksRequestHandler(shadowSocksConfiguration));
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration shadowSocksConfiguration = new ShadowSocksConfiguration();

        // 本地配置
        shadowSocksConfiguration.setType("aes-256-cfb");
        shadowSocksConfiguration.setPassword("1234567890");
        shadowSocksConfiguration.setPort(9898);

        // 反向代理服务配置
        shadowSocksConfiguration.setReverse(true);
        shadowSocksConfiguration.setReversePort(8786);

        // 指定代理内网端配置
        shadowSocksConfiguration.setProxyType("aes-256-cfb");
        shadowSocksConfiguration.setProxyPassword("1234567890");

        ShadowSocksApplication shadowSocksApplication = new OuterReverseShadowSocksApplication();
        shadowSocksApplication.start(shadowSocksConfiguration);
    }
}
