package org.leo.server.panama.core.connector.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.leo.server.panama.core.connector.Response;
import org.leo.server.panama.core.method.RequestMethod;

public class WebSocketUpgradeRequest extends NettyHttpRequest {
    private WebSocketServerHandshaker webSocketServerHandshaker;
    private ChannelHandlerContext context;
    private CloseWebSocketFrame closeWebSocketFrame;

    public WebSocketUpgradeRequest(ChannelHandlerContext ctx, HttpRequest request) {
        super(ctx, request);
        this.context = ctx;
    }

    @Override
    public void write(Response response) {
        if (response instanceof UpgradeResponse) {
            UpgradeResponse upgradeResponse = (UpgradeResponse) response;
            if (upgradeResponse.isUpgrade()) {
                String wsUrl = "ws://" + headers().get(HttpHeaderNames.HOST) + uri();
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                        wsUrl, null, false
                );

                webSocketServerHandshaker = wsFactory.newHandshaker(this);
                if (webSocketServerHandshaker == null) {
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
                } else {
                    webSocketServerHandshaker.handshake(context.channel(), this);
                }
            } else {
                HttpResponse httpResponse = new HttpResponse(response.getMessage());
                httpResponse.setAlive(false);
                httpResponse.setZip(true);
                super.write(httpResponse);
            }
        } else {
            super.write(response);
        }
    }

    @Override
    public void close() {
        if (null != webSocketServerHandshaker && closeWebSocketFrame != null) {
            webSocketServerHandshaker.close(context.channel(), closeWebSocketFrame.retain());
        } else {
            super.close();
        }

    }

    public void setCloseWebSocketFrame(CloseWebSocketFrame closeWebSocketFrame) {
        this.closeWebSocketFrame = closeWebSocketFrame;
    }

    @Override
    public RequestMethod requestMethod() {
        return RequestMethod.UPGRADE_WS;
    }

    @Override
    public String toString() {
        return "WebSocketUpgradeRequest{" +
                "webSocketServerHandshaker=" + uri() +
                ", context=" + context +
                ", closeWebSocketFrame=" + closeWebSocketFrame +
                '}';
    }
}
