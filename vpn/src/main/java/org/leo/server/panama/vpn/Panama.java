package org.leo.server.panama.vpn;

import org.leo.server.panama.vpn.application.ShadowSocksApplicationFactory;
import org.leo.server.panama.vpn.configuration.ConfigurationReader;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;

/**
 * @author xuyangze
 * @date 2019/7/17 5:54 PM
 */
public class Panama {
    public static void main(String []args) {
        String fileName = null;
        if (null != args && args.length > 0) {
            fileName = args[0];
        }

        ShadowSocksConfiguration shadowSocksConfiguration = ConfigurationReader.read(fileName);
        ShadowSocksApplicationFactory.startShadowSocksServer(shadowSocksConfiguration);
    }
}
