package org.leo.server.panama.vpn.proxy.impl;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.Logger;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.proxy.AbstractShadowSocksProxy;
import org.leo.server.panama.vpn.shadowsocks.ShadowSocksRequest;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;

import java.util.Arrays;

/**
 * @author xuyangze
 * @date 2018/11/20 8:13 PM
 */
public class ShadowSocksProxy extends AbstractShadowSocksProxy {
    private final static Logger log = Logger.getLogger(ShadowSocksProxy.class);

    public ShadowSocksProxy(Channel clientChannel,
                            Callback finish,
                            ShadowSocksConfiguration shadowSocksConfiguration,
                            NioEventLoopGroup eventLoopGroup,
                            ShadowsocksRequestResolver requestResolver) {
        super(clientChannel, finish, shadowSocksConfiguration, eventLoopGroup, requestResolver);
    }

    @Override
    public void doProxy(byte []data) {
        String target = null;
        int port = 0;

        log.info("client ---------------->  proxy " + data.length + " byte");
        byte []decryptData = wrapper.unwrap(data);
        // 分包后第二个包不再去解析，直接复用先前的连接
        if (null == redirectClient) {
            ShadowSocksRequest shadowSocksRequest = requestResolver.parse(decryptData);

            if (shadowSocksRequest.getAtyp() == ShadowSocksRequest.Type.UNKNOWN) {
                throw new RuntimeException("unknown request type: " + decryptData[0]);
            }

            target = shadowSocksRequest.getHost();
            port = shadowSocksRequest.getPort();

            if (shadowSocksRequest.getChannel() == ShadowSocksRequest.Channel.TCP) {
                int dataLength = shadowSocksRequest.getSubsequentDataLength();
                if (dataLength > 0) {
                    decryptData = Arrays.copyOfRange(decryptData, decryptData.length - dataLength, decryptData.length);
                }

            } else if (shadowSocksRequest.getChannel() == ShadowSocksRequest.Channel.UDP) {
                throw new RuntimeException("unsupport request type: udp");
            }
        }

        sendRequest2Target(decryptData, target, port);
    }
}