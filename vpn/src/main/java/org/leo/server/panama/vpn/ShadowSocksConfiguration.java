package org.leo.server.panama.vpn;

import java.util.Objects;

/**
 * @author xuyangze
 * @date 2018/10/10 下午3:16
 */
public class ShadowSocksConfiguration {
    // 加密类型
    private static String type;

    // 加密密码
    private static String password;

    // 代理服务器
    private static String proxy;

    // 代理服务器
    private static int proxyPort;

    // 代理服务器加密类型
    private static String proxyType;

    // 代理服务器加密密码
    private static String proxyPwd;

    public static String getType() {
        if (null == type) {
            return "aes-256-cfb";
        }
        return type;
    }

    public static void setType(String type) {
        ShadowSocksConfiguration.type = type;
    }

    public static String getPassword() {
        if (null == password) {
            return "123456";
        }

        return password;
    }

    public static void setPassword(String password) {
        ShadowSocksConfiguration.password = password;
    }

    public static String getProxy() {
        return proxy;
    }

    public static void setProxy(String proxy) {
        ShadowSocksConfiguration.proxy = proxy;
    }

    public static String getProxyType() {
        return proxyType;
    }

    public static void setProxyType(String proxyType) {
        ShadowSocksConfiguration.proxyType = proxyType;
    }

    public static String getProxyPwd() {
        return proxyPwd;
    }

    public static void setProxyPwd(String proxyPwd) {
        ShadowSocksConfiguration.proxyPwd = proxyPwd;
    }

    public static int getProxyPort() {
        return proxyPort;
    }

    public static void setProxyPort(int proxyPort) {
        ShadowSocksConfiguration.proxyPort = proxyPort;
    }

    public static boolean isProxyEnable() {
        return null != proxy && null != proxyType && null != proxyPwd;
    }

    public static boolean isProxyEqualsCurrent() {
        return Objects.equals(proxyType, type) && Objects.equals(proxyPwd, password);
    }
}
