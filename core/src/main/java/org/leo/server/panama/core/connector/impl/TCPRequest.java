package org.leo.server.panama.core.connector.impl;

import io.netty.channel.ChannelHandlerContext;
import org.leo.server.panama.core.method.RequestMethod;

public class TCPRequest extends NettyRequest {
    private byte []data;

    public TCPRequest(ChannelHandlerContext channelHandlerContext) {
        super(channelHandlerContext);
    }

    @Override
    public RequestMethod requestMethod() {
        return RequestMethod.TCP;
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
