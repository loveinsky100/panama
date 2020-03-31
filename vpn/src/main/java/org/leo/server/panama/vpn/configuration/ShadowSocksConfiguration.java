package org.leo.server.panama.vpn.configuration;

import java.util.Objects;

/**
 * @author xuyangze
 * @date 2018/10/10 下午3:16
 */
public class ShadowSocksConfiguration {

    /**
     * 启动模式
     * {@link ShadowSocksModeEnum}
     */
    private String mode;

    /**
     * encrypt: 加密 raw: 不加密
     * 加密信息： encrypt / raw
     */
    private String encrypt;

    /**
     * 加密类型
     */
    private String type;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 启动端口
     */
    private int port;

    /**
     * 代理服务器地址
     */
    private String proxy;

    /**
     * 代理服务器端口
     */
    private int proxyPort;

    /**
     * 代理服务器加密类型
     */
    private String proxyType;

    /**
     * 代理服务器加密密码
     */
    private String proxyPassword;

    /**
     * 反向代理服务器地址
     */
    private String reverseHost;

    /**
     * 反向代理服务器端口
     */
    private int reversePort;


    public String getEncrypt() {
        if (null == encrypt) {
            return "encrypt";
        }
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

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

    public int getPort() {
        if (0 == port) {
            return 9898;
        }

        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public boolean isProxyEqualsCurrent() {
        return Objects.equals(proxyType, type) && Objects.equals(proxyPassword, password);
    }

    public String getMode() {
        if (null == mode) {
            return ShadowSocksModeEnum.NORMAL.getMode();
        }

        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
