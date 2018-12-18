package org.leo.server.panama.core.handler.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import org.leo.server.panama.core.connector.impl.HttpRequest;
import org.leo.server.panama.core.connector.impl.NettyHttpRequest;
import org.leo.server.panama.core.handler.RequestHandler;

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {
    private HttpRequest httpRequest;
    private StringBuilder stringBuilder;
    private RequestHandler requestHandler;

    public HttpRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == stringBuilder) {
            stringBuilder = new StringBuilder();
        }

        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            io.netty.handler.codec.http.HttpRequest request = (io.netty.handler.codec.http.HttpRequest)msg;
            if (null == httpRequest) {
                httpRequest = new NettyHttpRequest(ctx, request);
            }

        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent)msg;
            ByteBuf byteBuf = content.content();
            byte []data = new byte[byteBuf.capacity()];
            byteBuf.readBytes(data);

            stringBuilder.append(new String(data));
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (null == stringBuilder) {
            return;
        }

        httpRequest.setMessage(stringBuilder.toString());
        doRequest(httpRequest);

        httpRequest = null;
        stringBuilder = null;

        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        stringBuilder = null;
        super.exceptionCaught(ctx, cause);
    }

    protected void doRequest(HttpRequest httpRequest) {
        this.requestHandler.doRequest(httpRequest);
    }
}
