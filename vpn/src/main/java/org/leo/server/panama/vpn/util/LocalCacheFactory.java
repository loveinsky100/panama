package org.leo.server.panama.vpn.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author xuyangze
 * @date 2018/10/10 下午2:40
 */
public class LocalCacheFactory {
    public static Cache createCache(long expire, int maxSize) {
        Cache<String, InetSocketAddress> cache = CacheBuilder.newBuilder()
                .maximumSize(2000)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build();

        return cache;
    }
}
