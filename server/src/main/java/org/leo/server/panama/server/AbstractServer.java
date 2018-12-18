package org.leo.server.panama.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Future;

public abstract class AbstractServer implements Server {
    private int port;
    private Channel serverChannel;

    public AbstractServer(int port) {
        this.port = port;
    }

    @Override
    public void start(int maxThread) {
        EventLoopGroup mainGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(maxThread);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(mainGroup, workGroup).
                channel(NioServerSocketChannel.class).
                childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        setupPipeline(ch.pipeline());
                    }
                });
        try {
            serverChannel = serverBootstrap.bind(port).sync().channel();
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public Future shutdown() {
        return serverChannel.close();
    }

    protected abstract void setupPipeline(ChannelPipeline pipeline);
}
