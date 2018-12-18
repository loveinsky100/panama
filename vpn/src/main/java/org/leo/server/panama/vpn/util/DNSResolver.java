package org.leo.server.panama.vpn.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author xuyangze
 * @date 2018/10/9 下午2:24
 */
public class DNSResolver {
    private final static Cache<String, InetSocketAddress> cache = LocalCacheFactory.createCache(5 * 60 * 1000, 2000);

    public static InetSocketAddress resolver(String host, int port) {
        String key = host + ":" + port;
        InetSocketAddress inetSocketAddress = cache.getIfPresent(key);
        if (null != inetSocketAddress) {
            return inetSocketAddress;
        }

        inetSocketAddress = new InetSocketAddress(host, port);
        cache.put(key, inetSocketAddress);
        return inetSocketAddress;
    }
}
