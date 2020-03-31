package org.leo.server.panama.vpn.application.impl;

import org.leo.server.panama.server.Server;
import org.leo.server.panama.vpn.application.ShadowSocksApplication;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.reverse.server.ReverseShadowSocksServer;
import java.io.IOException;

/**
 * 反向代理ss，部署在内网服务器，配合OuterReverseShadowSocks使用
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class InnerReverseShadowSocksApplication implements ShadowSocksApplication {
    @Override
    public void start(ShadowSocksConfiguration shadowSocksConfiguration) {
        Server server = new ReverseShadowSocksServer(shadowSocksConfiguration);
        server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
    }

    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration shadowSocksConfiguration = new ShadowSocksConfiguration();
        shadowSocksConfiguration.setType("aes-256-cfb");
        shadowSocksConfiguration.setPassword("1234567890");

        shadowSocksConfiguration.setReverseHost("127.0.0.1");
        shadowSocksConfiguration.setReversePort(8786);
        ShadowSocksApplication shadowSocksApplication = new InnerReverseShadowSocksApplication();
        shadowSocksApplication.start(shadowSocksConfiguration);
    }
}
