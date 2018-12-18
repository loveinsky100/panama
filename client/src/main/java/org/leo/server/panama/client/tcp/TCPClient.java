package org.leo.server.panama.client.tcp;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import org.leo.server.panama.client.AbstractClient;
import org.leo.server.panama.client.ClientResponseDelegate;
import org.leo.server.panama.client.handler.TCPClientHandler;
import org.leo.server.panama.core.connector.impl.TCPResponse;

public class TCPClient extends AbstractClient  {
    private ClientResponseDelegate<TCPResponse> delegate;

    public TCPClient(EventLoopGroup eventLoopGroup, ClientResponseDelegate<TCPResponse> delegate) {
        super(eventLoopGroup);
        this.delegate = delegate;
    }

    @Override
    protected void setupPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new TCPClientHandler(this, delegate));
    }
}
