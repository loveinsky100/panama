package org.leo.server.panama.vpn.configuration;

import java.util.Objects;

/**
 * @author xuyangze
 * @date 2018/10/10 下午3:16
 */
public class ShadowSocksConfiguration {
    // 加密类型
    private String type;

    // 加密密码
    private String password;

    // 代理服务器
    private String proxy;

    // 代理服务器
    private int proxyPort;

    // 代理服务器加密类型
    private String proxyType;

    // 代理服务器加密密码
    private String proxyPassword;

    // 是否创建反向代理服务器
    private boolean reverse;

    // 反向代理服务器地址
    private String reverseHost;

    // 反向代理服务器端口
    private int reversePort;

    public String getType() {
        if (null == type) {
            return "aes-256-cfb";
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        if (null == password) {
            return "123456";
        }

        return password;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public String getReverseHost() {
        return reverseHost;
    }

    public void setReverseHost(String reverseHost) {
        this.reverseHost = reverseHost;
    }

    public int getReversePort() {
        return reversePort;
    }

    public void setReversePort(int reversePort) {
        this.reversePort = reversePort;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isProxyEnable() {
        return null != proxy && null != proxyType && null != proxyPassword;
    }

    public boolean isProxyEqualsCurrent() {
        return Objects.equals(proxyType, type) && Objects.equals(proxyPassword, password);
    }
}
