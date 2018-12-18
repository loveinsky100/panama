package org.leo.server.panama.core.handler.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import org.leo.server.panama.core.connector.impl.NettyWebSocketRequest;
import org.leo.server.panama.core.connector.impl.WebSocketUpgradeRequest;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.core.handler.http.HttpRequestHandler;

public class HttpWebSocketRequestHandler extends HttpRequestHandler {
    private RequestHandler webSocketHandler;
    private StringBuilder stringBuilder;
    private boolean isWebSocket;
    private WebSocketUpgradeRequest webSocketUpgradeRequest;

    public HttpWebSocketRequestHandler(RequestHandler requestHandler, RequestHandler webSocketHandler) {
        super(requestHandler);
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            if (null == stringBuilder) {
                stringBuilder = new StringBuilder();
            }

            isWebSocket = true;
            handlerWebSocket(ctx, (WebSocketFrame)msg);
            ctx.fireChannelRead(msg);
        } else if (msg instanceof HttpRequest) {
            if (needUpgrade2WS(ctx, (HttpRequest) msg)) {
                doRequest(webSocketUpgradeRequest);
            } else {
                super.channelRead(ctx, msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (isWebSocket) {
            if (stringBuilder != null) {
                NettyWebSocketRequest nettyWebSocketRequest = new NettyWebSocketRequest(ctx, new TextWebSocketFrame(stringBuilder.toString()));
                nettyWebSocketRequest.setMessage(nettyWebSocketRequest.message());
                webSocketHandler.doRequest(nettyWebSocketRequest);
                stringBuilder = null;
            }

            ctx.fireChannelReadComplete();
        } else {
            super.channelReadComplete(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        isWebSocket = false;
        stringBuilder = null;
        super.exceptionCaught(ctx, cause);
    }

    private boolean needUpgrade2WS(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        // 判断是否升级websocke
        if (null == httpRequest.headers()) {
            return false;
        }

        String upData = httpRequest.headers().get(HttpHeaderNames.CONNECTION);
        String upMehod = httpRequest.headers().get(HttpHeaderNames.UPGRADE);
        if (null != upData
                && upData.equalsIgnoreCase("upgrade")
                && upMehod.equalsIgnoreCase("websocket")) {
            webSocketUpgradeRequest = new WebSocketUpgradeRequest(ctx, httpRequest);
            webSocketUpgradeRequest.setMessage("");
            return true;
        }

        return false;
    }

    private void handlerWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            if (null != webSocketUpgradeRequest) {
                webSocketUpgradeRequest.setCloseWebSocketFrame((CloseWebSocketFrame) frame);
                webSocketUpgradeRequest.close();
            } else {
                ctx.close();
            }

            return;
        }

        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // 文本消息，不支持二进制消息
        if (frame instanceof TextWebSocketFrame) {
            stringBuilder.append(((TextWebSocketFrame)frame).text());
        }
    }
}
