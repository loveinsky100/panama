package org.leo.server.panama.vpn.proxy;

import org.leo.server.panama.core.connector.Request;

/**
 * @author xuyangze
 * @date 2018/11/20 8:16 PM
 */
public interface Proxy<T extends Request> {
    void doProxy(T request);
}
