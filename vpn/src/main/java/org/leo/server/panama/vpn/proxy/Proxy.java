package org.leo.server.panama.vpn.proxy;

/**
 * @author xuyangze
 * @date 2018/11/20 8:16 PM
 */
public interface Proxy {
    /**
     * 代理
     * @param data
     */
    void doProxy(byte []data);
}
