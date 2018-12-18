package org.leo.server.panama.core.connector.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.leo.server.panama.core.connector.Response;
import org.leo.server.panama.core.method.RequestMethod;

public class NettyWebSocketRequest extends NettyRequest implements WebSocketRequest {
    private TextWebSocketFrame textWebSocketFrame;

    public NettyWebSocketRequest() {
        super();
    }

    public NettyWebSocketRequest(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) {
        super(ctx);
        this.textWebSocketFrame = textWebSocketFrame;
    }

    @Override
    public String message() {
        return textWebSocketFrame.text();
    }

    @Override
    public void write(Response response) {
        TextWebSocketFrame responseFrame = new TextWebSocketFrame(response.getMessage());
        writeMsg(responseFrame);
    }

    @Override
    public RequestMethod requestMethod() {
        return RequestMethod.WS;
    }

    @Override
    public String toString() {
        return "NettyWebSocketRequest{" +
                "textWebSocketFrame=" + message() +
                '}';
    }
}
