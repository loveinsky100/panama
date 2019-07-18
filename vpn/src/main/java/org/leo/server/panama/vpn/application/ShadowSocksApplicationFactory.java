package org.leo.server.panama.vpn.application;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.log4j.Logger;
import org.leo.server.panama.vpn.application.impl.InnerReverseShadowSocksApplication;
import org.leo.server.panama.vpn.application.impl.NormalShadowSocksApplication;
import org.leo.server.panama.vpn.application.impl.OuterReverseShadowSocksApplication;
import org.leo.server.panama.vpn.application.impl.ProxyShadowSocksApplication;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.configuration.ShadowSocksModeEnum;
import org.leo.server.panama.vpn.util.FileUtils;

/**
 * @author xuyangze
 * @date 2019/7/17 5:59 PM
 */
public class ShadowSocksApplicationFactory {
    private final static Logger log = Logger.getLogger(ShadowSocksApplicationFactory.class);

    private static final String BANNER = "panama.banner";

    public static ShadowSocksApplication create(String mode) {
        ShadowSocksModeEnum shadowSocksModeEnum = ShadowSocksModeEnum.formMode(mode);
        if (null == shadowSocksModeEnum) {
            return null;
        }

        switch (shadowSocksModeEnum) {
            case PROXY: return new ProxyShadowSocksApplication();
            case NORMAL: return new NormalShadowSocksApplication();
            case REVERSE_INNER: return new InnerReverseShadowSocksApplication();
            case REVERSE_OUTER: return new OuterReverseShadowSocksApplication();
            default: return null;
        }
    }

    public static void startShadowSocksServer(ShadowSocksConfiguration shadowSocksConfiguration) {
        ShadowSocksApplication shadowSocksApplication = create(shadowSocksConfiguration.getMode());
        if (null == shadowSocksApplication) {
            throw new RuntimeException("Panama config error");
        }

        String panama = FileUtils.readFromResource(BANNER);
        log.info("\n" +
                panama +
                ":: ShadowSocks - " + shadowSocksConfiguration.getMode() + "\n" +
                ":: Port        - " + shadowSocksConfiguration.getPort() + "\n" +
                ":: Type        - " + shadowSocksConfiguration.getType() + "\n" +
                ":: Password    - " + shadowSocksConfiguration.getPassword());
        shadowSocksApplication.start(shadowSocksConfiguration);
    }
}
