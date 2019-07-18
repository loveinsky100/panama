package org.leo.server.panama.vpn.application.impl;

import org.leo.server.panama.vpn.application.ShadowSocksApplication;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import java.io.IOException;

/**
 * 普通的ss
 * @author xuyangze
 * @date 2018/10/9 下午1:16
 */
public class ProxyShadowSocksProxyDemo {
    public static void main(String []args) throws IOException {
        ShadowSocksConfiguration shadowSocksConfiguration = new ShadowSocksConfiguration();

        shadowSocksConfiguration.setType("aes-256-cfb");
        shadowSocksConfiguration.setPassword("123456789");
        shadowSocksConfiguration.setPort(9899);

        ShadowSocksApplication shadowSocksApplication = new NormalShadowSocksApplication();
        shadowSocksApplication.start(shadowSocksConfiguration);
    }
}
