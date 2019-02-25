package org.leo.server.panama.vpn.proxy.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.Logger;
import org.leo.server.panama.client.Client;
import org.leo.server.panama.core.connector.impl.TCPResponse;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.proxy.AbstractShadowSocksProxy;
import org.leo.server.panama.vpn.reverse.client.ReverseTCPClient;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreServer;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;

/**
 * @author xuyangze
 * @date 2018/11/20 8:13 PM
 */
public class Redirect2ReverseShadowSocksProxy extends AbstractShadowSocksProxy {
    private final static Logger log = Logger.getLogger(Redirect2ReverseShadowSocksProxy.class);

    private final String LOCAL_ADDRESS = "127.0.0.1";

    private ReverseCoreServer reverseCoreServer;

    public Redirect2ReverseShadowSocksProxy(Channel clientChannel,
                                            Callback finish,
                                            String encryption,
                                            String password,
                                            NioEventLoopGroup eventLoopGroup,
                                            ShadowsocksRequestResolver requestResolver,
                                            ReverseCoreServer reverseCoreServer) {
        super(clientChannel, finish, encryption, password, eventLoopGroup, requestResolver);
        this.reverseCoreServer = reverseCoreServer;
    }

    @Override
    public void doCompleteResponse(Client client, TCPResponse response) {
        // target -> proxy -> client
        log.info(" proxy <---------------- target " + response.getData().length + " byte");
        send2Client(response.getData());
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
    protected void send2Client(byte[] data) {
        // 不需要进行加密，直接返回
        clientChannel.write(Unpooled.wrappedBuffer(data));
        clientChannel.flush();
        log.info("client <----------------  proxy " + data.length + " byte");
    }

    @Override
    public void doProxy(byte []data) {
        // 不需要解密，直接透传给下一个节点
        log.info("client ---------------->  proxy " + data.length + " byte");

        // 此处的host和port请忽略，实际上的client为一个反向代理服务器ReverseTCPClient
        sendRequest2Target(data, LOCAL_ADDRESS, 8080);
    }

    @Override
    protected Client createClient(EventLoopGroup eventLoopGroup) {
        // reverseCoreServer是一个已经启动的服务器
        return new ReverseTCPClient(this, reverseCoreServer);
    }
}