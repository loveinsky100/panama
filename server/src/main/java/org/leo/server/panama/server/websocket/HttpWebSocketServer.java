package org.leo.server.panama.server.websocket;

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.core.handler.websocket.HttpWebSocketRequestHandler;
import org.leo.server.panama.server.http.HttpServer;

public class HttpWebSocketServer extends HttpServer {
    private RequestHandler webSocketHandler;

    public HttpWebSocketServer(int port, RequestHandler httpHandler, RequestHandler webSocketHandler) {
        super(port, httpHandler);
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    protected ChannelInboundHandlerAdapter handlerAdapter(RequestHandler requestHandler) {
        return new HttpWebSocketRequestHandler(requestHandler, webSocketHandler);
    }
}