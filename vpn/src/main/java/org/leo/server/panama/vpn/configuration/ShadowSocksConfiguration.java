package org.leo.server.panama.vpn.configuration;

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
    private static String proxyPassword;

    //
    private static boolean reverse;

    //
    private static String reverseHost;

    //
    private static int reversePort;

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

    public static boolean isReverse() {
        return reverse;
    }

    public static void setReverse(boolean reverse) {
        ShadowSocksConfiguration.reverse = reverse;
    }

    public static String getReverseHost() {
        return reverseHost;
    }

    public static void setReverseHost(String reverseHost) {
        ShadowSocksConfiguration.reverseHost = reverseHost;
    }

    public static int getReversePort() {
        return reversePort;
    }

    public static void setReversePort(int reversePort) {
        ShadowSocksConfiguration.reversePort = reversePort;
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

    public static String getProxyPassword() {
        return proxyPassword;
    }

    public static void setProxyPassword(String proxyPassword) {
        ShadowSocksConfiguration.proxyPassword = proxyPassword;
    }

    public static int getProxyPort() {
        return proxyPort;
    }

    public static void setProxyPort(int proxyPort) {
        ShadowSocksConfiguration.proxyPort = proxyPort;
    }

    public static boolean isProxyEnable() {
        return null != proxy && null != proxyType && null != proxyPassword;
    }

    public static boolean isProxyEqualsCurrent() {
        return Objects.equals(proxyType, type) && Objects.equals(proxyPassword, password);
    }
}
