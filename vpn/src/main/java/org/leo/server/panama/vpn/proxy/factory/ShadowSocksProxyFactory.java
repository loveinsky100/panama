package org.leo.server.panama.vpn.proxy.factory;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.leo.server.panama.vpn.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.constant.VPNConstant;
import org.leo.server.panama.vpn.proxy.TCPProxy;
import org.leo.server.panama.vpn.proxy.impl.AgentShadowSocksProxy;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;
import org.leo.server.panama.vpn.proxy.impl.ShadowSocksProxy;

/**
 * @author xuyangze
 * @date 2018/11/20 8:19 PM
 */
public class ShadowSocksProxyFactory {
    // 发送请求给代理服务器
    private static NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(VPNConstant.MAX_CLIENT_THREAD_COUNT);

    // 代理服务请求返回数据解析
    private static ShadowsocksRequestResolver requestResolver = new ShadowsocksRequestResolver();

    public static TCPProxy create(Channel channel, Callback callback) {
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
