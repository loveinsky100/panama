package org.leo.server.panama.vpn.proxy.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.Logger;
import org.leo.server.panama.client.Client;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;

import java.util.function.Function;

/**
 * @author xuyangze
 * @date 2018/11/20 8:13 PM
 */
public class ReverseShadowSocksProxy extends ShadowSocksProxy {
    private final static Logger log = Logger.getLogger(ReverseShadowSocksProxy.class);
    private Function<byte[], ByteBuf> appendTagFunc;

    public ReverseShadowSocksProxy(Channel clientChannel, Callback finish, String encryption, String password, NioEventLoopGroup eventLoopGroup, ShadowsocksRequestResolver requestResolver) {
        super(clientChannel, finish, encryption, password, eventLoopGroup, requestResolver);
    }

    @Override
    protected void send2Client(byte[] data) {
        data = wrapper.wrap(data);

        // 返回的结果会添加tag标记，此tag为代理请求的tag
        clientChannel.write(appendTagFunc.apply(data));
        clientChannel.flush();
        log.info("client <----------------  proxy " + data.length + " byte");
    }

    @Override
    public void onConnectClosed(Client client) {
        // send close data
        log.info("client <----------------  proxy closed");
        if (null != finish) {
            try {
                finish.call();
            } catch (Exception e) {
                //
            }
        }
    }

    public Function<byte[], ByteBuf> getAppendTagFunc() {
        return appendTagFunc;
    }

    public void setAppendTagFunc(Function<byte[], ByteBuf> appendTagFunc) {
        this.appendTagFunc = appendTagFunc;
    }
}