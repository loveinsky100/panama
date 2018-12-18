package org.leo.server.panama.server.http;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.core.handler.http.HttpRequestHandler;
import org.leo.server.panama.server.AbstractServer;

public class HttpServer extends AbstractServer {

    private RequestHandler requestHandler;

    public HttpServer(int port, RequestHandler requestHandler) {
        super(port);
        this.requestHandler = requestHandler;
    }

    protected ChannelInboundHandlerAdapter handlerAdapter(RequestHandler requestHandler) {
        return new HttpRequestHandler(requestHandler);
    }

    protected void setupPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(handlerAdapter(requestHandler));
    }
}