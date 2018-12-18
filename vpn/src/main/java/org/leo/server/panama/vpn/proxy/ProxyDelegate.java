package org.leo.server.panama.vpn.proxy;

/**
 * @author xuyangze
 * @date 2018/11/20 8:16 PM
 */
public interface ProxyDelegate {
    void proxyDidReceiveDatFromTagrt(Proxy proxy, byte []data);
    void proxyDidClose2Tagrt(Proxy proxy, byte []data);
}
