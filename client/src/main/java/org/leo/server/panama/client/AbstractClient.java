package org.leo.server.panama.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public abstract class AbstractClient implements Client {
    private volatile boolean close = true;
    private ChannelFuture connectFuture;
    private EventLoopGroup workGroup;

    public AbstractClient(EventLoopGroup eventLoopGroup) {
        this.workGroup = eventLoopGroup;
    }

    @Override
    public Client connect(InetSocketAddress inetSocketAddress) {
        if (!close) {
            return this;
        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup).
                channel(NioSocketChannel.class).
                handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        setupPipeline(ch.pipeline());
                    }
                });
        try {
            close = false;
            connectFuture = bootstrap.connect(inetSocketAddress).sync();
            connectFuture.addListener((future) -> {
//                System.out.println("operationComplete");
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return this;
    }

    protected abstract void setupPipeline(ChannelPipeline pipeline);

    @Override
    public void send(byte []data, int timeout) {
        try {
            connectFuture.channel().writeAndFlush(Unpooled.wrappedBuffer(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isClose() {
        return close;
    }

    @Override
    public void close() {
        try {
            connectFuture.channel().close().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close = true;
            workGroup = null;
        }
    }
}
