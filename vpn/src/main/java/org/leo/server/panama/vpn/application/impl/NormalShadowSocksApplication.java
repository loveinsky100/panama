package org.leo.server.panama.vpn.application.impl;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.vpn.application.ShadowSocksApplication;
import org.leo.server.panama.vpn.configuration.ConfigurationReader;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.handler.ShadowSocksRequestHandler;
import java.io.IOException;

/**
 * 配置如下
 *
 * {
 * 	"password":"1234567890",
 * 	"port":9898,
 * 	"type":"aes-256-cfb"
 * }
 *
 * 普通的ss，直接启动即可
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class NormalShadowSocksApplication implements ShadowSocksApplication {
    @Override
    public void start(ShadowSocksConfiguration shadowSocksConfiguration) {
        Server server = new TCPServer(shadowSocksConfiguration.getPort(), new ShadowSocksRequestHandler(shadowSocksConfiguration));
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration shadowSocksConfiguration = ConfigurationReader.read();

        ShadowSocksApplication shadowSocksApplication = new NormalShadowSocksApplication();
        shadowSocksApplication.start(shadowSocksConfiguration);
    }
}
