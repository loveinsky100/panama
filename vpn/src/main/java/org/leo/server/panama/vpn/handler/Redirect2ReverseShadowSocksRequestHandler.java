package org.leo.server.panama.vpn.handler;

import io.netty.channel.Channel;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.proxy.TCPProxy;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;

public class Redirect2ReverseShadowSocksRequestHandler extends AgentShadowSocksRequestHandler {
    public Redirect2ReverseShadowSocksRequestHandler(ShadowSocksConfiguration shadowSocksConfiguration) {
        super(shadowSocksConfiguration);
        ShadowSocksProxyFactory.createReverseServer(shadowSocksConfiguration);
    }

    @Override
    protected TCPProxy createProxy(Channel channel, ShadowSocksConfiguration shadowSocksConfiguration) {
        return ShadowSocksProxyFactory.createRedirect2ReverseShadowSocksProxy(
                channel,
                () -> this.close(channel),
                shadowSocksConfiguration);
    }
}
