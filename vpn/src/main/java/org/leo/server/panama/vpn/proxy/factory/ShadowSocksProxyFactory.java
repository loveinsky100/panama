package org.leo.server.panama.vpn.proxy.factory;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.proxy.TCPProxy;
import org.leo.server.panama.vpn.proxy.impl.AgentShadowSocksProxy;
import org.leo.server.panama.vpn.proxy.impl.Redirect2ReverseShadowSocksProxy;
import org.leo.server.panama.vpn.proxy.impl.ReverseShadowSocksProxy;
import org.leo.server.panama.vpn.proxy.impl.ShadowSocksProxy;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreServer;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;

/**
 * @author xuyangze
 * @date 2018/11/20 8:19 PM
 */
public class ShadowSocksProxyFactory {
    // 发送请求给代理服务器
    private static NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(VPNConstant.MAX_CLIENT_THREAD_COUNT);

    // 代理服务请求返回数据解析
    private static ShadowsocksRequestResolver requestResolver = new ShadowsocksRequestResolver();

    //
    private static ReverseCoreServer reverseCoreServer = new ReverseCoreServer(8080);

    public static void start() {
        new Thread(() -> {
            reverseCoreServer.start(100);
        }).start();
    }

    public static TCPProxy create(Channel channel, Callback callback) {
        if (ShadowSocksConfiguration.isReverse()) {
            return new Redirect2ReverseShadowSocksProxy(
                    channel,
                    callback,
                    ShadowSocksConfiguration.getType(),
                    ShadowSocksConfiguration.getPassword(),
                    eventLoopGroup,
                    requestResolver,
                    reverseCoreServer);
        }

        if (null != ShadowSocksConfiguration.getReverseHost()) {
            return new ReverseShadowSocksProxy(
                    channel,
                    callback,
                    ShadowSocksConfiguration.getType(),
                    ShadowSocksConfiguration.getPassword(),
                    eventLoopGroup,
                    requestResolver);
        }

        if (ShadowSocksConfiguration.isProxyEnable()) {
            return new AgentShadowSocksProxy(
                    channel,
                    callback,
                    ShadowSocksConfiguration.getType(),
                    ShadowSocksConfiguration.getPassword(),
                    eventLoopGroup,
                    requestResolver);
        } else {
            return new ShadowSocksProxy(
                    channel,
                    callback,
                    ShadowSocksConfiguration.getType(),
                    ShadowSocksConfiguration.getPassword(),
                    eventLoopGroup,
                    requestResolver);
        }
    }
}
