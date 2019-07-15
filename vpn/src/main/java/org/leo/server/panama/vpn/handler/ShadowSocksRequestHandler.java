package org.leo.server.panama.vpn.handler;

import com.google.common.cache.Cache;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.leo.server.panama.core.connector.impl.TCPRequest;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.proxy.TCPProxy;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;
import org.leo.server.panama.vpn.util.LocalCacheFactory;

public class ShadowSocksRequestHandler implements RequestHandler<TCPRequest> {
    private final static Logger log = Logger.getLogger(AgentShadowSocksRequestHandler.class);

    // 请求和代理缓存
    private Cache<Channel, TCPProxy> channel2ProxyCache = LocalCacheFactory.createCache(5 * 60 * 1000, 2000);

    private ShadowSocksConfiguration shadowSocksConfiguration;
    public ShadowSocksRequestHandler(ShadowSocksConfiguration shadowSocksConfiguration) {
        this.shadowSocksConfiguration = shadowSocksConfiguration;
    }

    protected TCPProxy createProxy(Channel channel, ShadowSocksConfiguration shadowSocksConfiguration) {
        return ShadowSocksProxyFactory.createShadowSocksProxy(
                channel,
                () -> this.close(channel),
                shadowSocksConfiguration);
    }

    protected void close(Channel channel) {
        channel2ProxyCache.invalidate(channel);
    }

    @Override
    public void doRequest(TCPRequest request) {
        Channel channel = request.getChannelHandlerContext().channel();
        TCPProxy proxy = channel2ProxyCache.getIfPresent(channel);
        if (null == proxy) {
            proxy = createProxy(channel, shadowSocksConfiguration);
            channel2ProxyCache.put(channel, proxy);
        }

        proxy.doProxy(request.getData());
    }
}
