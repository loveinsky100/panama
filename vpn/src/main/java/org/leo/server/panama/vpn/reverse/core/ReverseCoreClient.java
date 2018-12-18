package org.leo.server.panama.vpn.reverse.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.Logger;
import org.leo.server.panama.client.Client;
import org.leo.server.panama.client.ClientResponseDelegate;
import org.leo.server.panama.client.handler.TCPClientHandler;
import org.leo.server.panama.client.tcp.TCPClient;
import org.leo.server.panama.core.connector.impl.TCPResponse;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * 反向代理客户端，发送请求到代理端，代理端返回数据
 * 服务端向被代理端进行长链接，服务端维持和代理端的连接
 * 代理端收到客户端请求，通过长链接发送请求到服务端
 * 服务端转发请求到服务端的ss端口，请求返回后返回给代理端
 * @author xuyangze
 * @date 2018/11/21 3:17 PM
 */
public class ReverseCoreClient extends TCPClient implements ClientResponseDelegate<TCPResponse> {
    private final static Logger log = Logger.getLogger(ReverseCoreClient.class);

    private InetSocketAddress address;
    private Consumer<byte []> consumer;

    public ReverseCoreClient(InetSocketAddress address, Consumer<byte []> consumer) {
        super(new NioEventLoopGroup(1), null);
        this.address = address;
        this.consumer = consumer;
    }

    public Channel channel() {
        return getConnectFuture().channel();
    }

    @Override
    public Client connect(InetSocketAddress inetSocketAddress) {
        log.info("ReverseCoreClient try connect to server: " + address.getHostName());
        return super.connect(inetSocketAddress);
    }

    @Override
    protected void setupPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new TCPClientHandler(this, this));
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
    public void onResponseComplete(Client client) {

    }

    @Override
    public void doCompleteResponse(Client client, TCPResponse response) {

    }

    @Override
    public void doPerResponse(Client client, TCPResponse response) {
        consumer.accept(response.getData());
    }

    @Override
    public void onConnectClosed(Client client) {
        this.connect(address);
    }
}
