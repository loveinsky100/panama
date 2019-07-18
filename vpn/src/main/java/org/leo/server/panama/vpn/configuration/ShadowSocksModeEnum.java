package org.leo.server.panama.vpn.configuration;

import java.util.Objects;

/**
 * @author xuyangze
 * @date 2019/7/17 5:45 PM
 */
public enum ShadowSocksModeEnum {
    /**
     * 正常
     */
    NORMAL("normal"),

    /**
     * 代理模式
     */
    PROXY("proxy"),

    /**
     * 内网穿透模式，内网服务
     */
    REVERSE_INNER("inner"),

    /**
     * 内网穿透模式，外网服务
     */
    REVERSE_OUTER("outer"),
    ;

    private String mode;

    ShadowSocksModeEnum(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static ShadowSocksModeEnum formMode(String mode) {
        for (ShadowSocksModeEnum shadowSocksModeEnum : ShadowSocksModeEnum.values()) {
            if (Objects.equals(mode, shadowSocksModeEnum.mode)) {
                return shadowSocksModeEnum;
            }
        }

        return null;
    }
}
