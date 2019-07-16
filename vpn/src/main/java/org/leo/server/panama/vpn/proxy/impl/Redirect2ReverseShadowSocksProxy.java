package org.leo.server.panama.vpn.proxy.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.Logger;
import org.leo.server.panama.client.Client;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.reverse.client.ReverseTCPClient;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreServer;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;

/**
 * @author xuyangze
 * @date 2018/11/20 8:13 PM
 */
public class Redirect2ReverseShadowSocksProxy extends AgentShadowSocksProxy {
    private final static Logger log = Logger.getLogger(Redirect2ReverseShadowSocksProxy.class);

    private final String LOCAL_ADDRESS = "127.0.0.1";

    private ReverseCoreServer reverseCoreServer;

    public Redirect2ReverseShadowSocksProxy(Channel clientChannel,
                                            Callback finish,
                                            ShadowSocksConfiguration shadowSocksConfiguration,
                                            NioEventLoopGroup eventLoopGroup,
                                            ShadowsocksRequestResolver requestResolver,
                                            ReverseCoreServer reverseCoreServer) {
        super(clientChannel, finish, shadowSocksConfiguration, eventLoopGroup, requestResolver);
        this.reverseCoreServer = reverseCoreServer;
    }

    @Override
    public boolean shouldDoPerResponse() {
        return false;
    }

    @Override
    public boolean shouldDoCompleteResponse() {
        return true;
    }

    @Override
    public void doProxy(byte []data) {
        // 此处的host和port请忽略，实际上的client为一个反向代理服务器ReverseTCPClient
        doProxy(data, LOCAL_ADDRESS, 8080);
    }

    @Override
    protected Client createClient(EventLoopGroup eventLoopGroup) {
        // reverseCoreServer是一个已经启动的服务器
        return new ReverseTCPClient(this, reverseCoreServer);
    }
}