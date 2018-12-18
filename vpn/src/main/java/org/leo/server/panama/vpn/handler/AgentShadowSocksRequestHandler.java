package org.leo.server.panama.vpn.handler;

import com.google.common.cache.Cache;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.leo.server.panama.core.connector.impl.TCPRequest;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.vpn.proxy.TCPProxy;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;
import org.leo.server.panama.vpn.util.LocalCacheFactory;

public class AgentShadowSocksRequestHandler implements RequestHandler<TCPRequest> {
    private final static Logger log = Logger.getLogger(AgentShadowSocksRequestHandler.class);

    // 请求和代理缓存
    private Cache<Channel, TCPProxy> channel2ProxyCache = LocalCacheFactory.createCache(5 * 60 * 1000, 2000);

    @Override
    public void doRequest(TCPRequest request) {
        Channel channel = request.getChannelHandlerContext().channel();
        TCPProxy proxy = channel2ProxyCache.getIfPresent(channel);
        if (null == proxy) {
            proxy = ShadowSocksProxyFactory.create(request.getChannelHandlerContext().channel(), () -> channel2ProxyCache.invalidate(channel));
            channel2ProxyCache.put(channel, proxy);
        }

        proxy.doProxy(request.getData());
    }

}
