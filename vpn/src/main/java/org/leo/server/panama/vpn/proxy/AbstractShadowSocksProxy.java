package org.leo.server.panama.vpn.proxy;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.Logger;
import org.leo.server.panama.client.Client;
import org.leo.server.panama.client.ClientResponseDelegate;
import org.leo.server.panama.client.tcp.TCPClient;
import org.leo.server.panama.core.connector.impl.TCPResponse;
import org.leo.server.panama.vpn.security.wrapper.Wrapper;
import org.leo.server.panama.vpn.security.wrapper.WrapperFactory;
import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
import org.leo.server.panama.vpn.util.Callback;
import org.leo.server.panama.vpn.util.DNSResolver;

/**
 * @author xuyangze
 * @date 2018/11/20 8:13 PM
 */
public abstract class AbstractShadowSocksProxy implements ClientResponseDelegate<TCPResponse>, TCPProxy {
    private final static Logger log = Logger.getLogger(AbstractShadowSocksProxy.class);

    protected Channel clientChannel;
    protected Wrapper wrapper;
    protected Client redirectClient;
    protected Callback finish;
    // 发送请求给代理服务器
    protected NioEventLoopGroup eventLoopGroup;

    // 代理服务请求返回数据解析
    protected ShadowsocksRequestResolver requestResolver;

    public AbstractShadowSocksProxy(Channel clientChannel, Callback finish, String encryption, String password, NioEventLoopGroup eventLoopGroup, ShadowsocksRequestResolver requestResolver) {
        this.clientChannel = clientChannel;
        wrapper = WrapperFactory.getInstance(encryption, password, "encrypt");
        this.finish = finish;
        this.eventLoopGroup = eventLoopGroup;
        this.requestResolver = requestResolver;
    }

    @Override
    public boolean shouldDoPerResponse() {
        return true;
    }

    @Override
    public boolean shouldDoCompleteResponse() {
        return false;
    }

    @Override
    public void doPerResponse(Client client, TCPResponse response) {
        // target -> proxy -> client
        log.info(" proxy <---------------- target " + response.getData().length + " byte");
        send2Client(response.getData());
    }

    @Override
    public void onConnectClosed(Client client) {
        // send close data
        log.info("client <----------------  proxy closed");
        clientChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        if (null != finish) {
            try {
                finish.call();
            } catch (Exception e) {
                //
            }
        }
    }

    protected void send2Client(byte []data) {
        data = wrapper.wrap(data);
        clientChannel.write(Unpooled.wrappedBuffer(data));
        clientChannel.flush();
        log.info("client <----------------  proxy " + data.length + " byte");
    }

    protected void sendRequest2Target(byte []data, String target, int port) {
        if (null == redirectClient) {
            log.info(" proxy ----------------> target " + target + ":" + port);
            redirectClient = createClient(eventLoopGroup);
            redirectClient.connect(DNSResolver.resolver(target, port));
        }

        redirectClient.send(data, 0);
        log.info(" proxy ----------------> target " + data.length + " byte");
    }

    protected Client createClient(EventLoopGroup eventLoopGroup) {
        return new TCPClient(eventLoopGroup, this);
    }
}