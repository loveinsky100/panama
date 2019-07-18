package org.leo.server.panama.vpn.application;

import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;

/**
 * @author xuyangze
 * @date 2019/7/17 5:55 PM
 */
public interface ShadowSocksApplication {

    /**
     * 启动ShadowSocks服务
     * @param shadowSocksConfiguration
     */
    void start(ShadowSocksConfiguration shadowSocksConfiguration);
}
