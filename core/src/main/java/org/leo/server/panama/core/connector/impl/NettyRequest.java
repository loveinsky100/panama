package org.leo.server.panama.core.connector.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.leo.server.panama.core.connector.Request;
import org.leo.server.panama.core.connector.Response;

import java.net.InetSocketAddress;

public abstract class NettyRequest extends FunctionAttributeRequest implements Request, Function {
    private ChannelHandlerContext channelHandlerContext;
    private InetSocketAddress insocket;

    public NettyRequest() {
    }

    public NettyRequest(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        this.insocket = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
    }

    @Override
    public void writeMsg(Object msg) {
        channelHandlerContext.write(msg);
    }

    @Override
    public void write(Response response) {
        byte []data = response.getData();
        if (null == data) {
            return;
        }

        writeMsg(Unpooled.wrappedBuffer(data));
    }

    @Override
    public void close() {
        channelHandlerContext.close();
    }

    @Override
    public void flush() {
        channelHandlerContext.flush();
    }

    @Override
    public String clientIp() {
        return insocket.getHostName();
    }

    @Override
    public int clientPort() {
        return insocket.getPort();
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    @Override
    public String toString() {
        return "NettyRequest{" +
                "channelHandlerContext=" + channelHandlerContext +
                ", insocket=" + insocket +
                '}';
    }
}
