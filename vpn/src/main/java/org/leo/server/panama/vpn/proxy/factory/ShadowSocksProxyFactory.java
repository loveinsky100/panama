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

    // 创建反向代理服务器
    private static ReverseCoreServer reverseCoreServer;

    /**
     * 开启反向代理服务
     */
    public static void createReverseServer(ShadowSocksConfiguration shadowSocksConfiguration) {
        reverseCoreServer = new ReverseCoreServer(shadowSocksConfiguration.getReversePort());

        new Thread(() -> {
            reverseCoreServer.start(100);
        }).start();
    }

    public static TCPProxy createReverseShadowSocksProxy(Channel channel, Callback callback, ShadowSocksConfiguration shadowSocksConfiguration) {
        // 反响代理TCP服务
        return new ReverseShadowSocksProxy(
                channel,
                callback,
                shadowSocksConfiguration,
                eventLoopGroup,
                requestResolver);
    }

    public static TCPProxy createRedirect2ReverseShadowSocksProxy(Channel channel, Callback callback, ShadowSocksConfiguration shadowSocksConfiguration) {
        // 反响代理TCP服务
        return new Redirect2ReverseShadowSocksProxy(
                channel,
                callback,
                shadowSocksConfiguration,
                eventLoopGroup,
                requestResolver,
                reverseCoreServer);
    }

    public static TCPProxy createAgentShadowSocksProxy(Channel channel, Callback callback, ShadowSocksConfiguration shadowSocksConfiguration) {
        return new AgentShadowSocksProxy(
                channel,
                callback,
                shadowSocksConfiguration,
                eventLoopGroup,
                requestResolver);
    }

    public static TCPProxy createShadowSocksProxy(Channel channel, Callback callback, ShadowSocksConfiguration shadowSocksConfiguration) {
        return new ShadowSocksProxy(
                channel,
                callback,
                shadowSocksConfiguration,
                eventLoopGroup,
                requestResolver);
    }
}
