package org.leo.server.panama.server.tcp;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.core.handler.tcp.TCPRequestHandler;
import org.leo.server.panama.server.AbstractServer;

public class TCPServer extends AbstractServer {

    private RequestHandler requestHandler;

    public TCPServer(int port, RequestHandler requestHandler) {
        super(port);
        this.requestHandler = requestHandler;
    }

    public TCPServer(int port) {
        super(port);
    }

    @Override
    protected void setupPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new TCPRequestHandler(requestHandler));
    }
}
