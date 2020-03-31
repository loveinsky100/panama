package org.leo.server.panama.vpn.proxy.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.Logger;
import org.leo.server.panama.client.Client;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;

import java.util.function.Function;

/**
 * 收到后重新返回给发送方，测试用
 * @author xuyangze
 * @date 2018/11/20 8:13 PM
 */
public class RePlayShadowSocksProxy extends ShadowSocksProxy {
    private final static Logger log = Logger.getLogger(RePlayShadowSocksProxy.class);
    private Function<byte[], ByteBuf> appendTagFunc;

    public RePlayShadowSocksProxy(Channel clientChannel,
                                   Callback finish,
                                   ShadowSocksConfiguration shadowSocksConfiguration,
                                   NioEventLoopGroup eventLoopGroup,
                                   ShadowsocksRequestResolver requestResolver) {
        super(clientChannel, finish, shadowSocksConfiguration, eventLoopGroup, requestResolver);
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

    public void closeTargetConnection() {
        if (null != this.redirectClient) {
            this.redirectClient.close();
        }
    }

    @Override
    public void doProxy(byte[] data) {
        log.info("client ---------------->  proxy " + data.length + " byte, data is :" + new String(data));
        send2Client(data);
    }
}