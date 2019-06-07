package org.leo.server.panama.core.handler.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.leo.server.panama.core.connector.Request;
import org.leo.server.panama.core.connector.impl.TCPRequest;
import org.leo.server.panama.core.handler.RequestHandler;
import java.util.Arrays;

public class TCPRequestHandler extends ChannelInboundHandlerAdapter {

    private Request request;
    private byte []data;
    private RequestHandler requestHandler;

    public TCPRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        requestHandler.onConnect(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        data = null;
        requestHandler.onClose(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == request) {
            request = new TCPRequest(ctx);
        }

        byte []readData = read(ctx, msg);
        if (null == data || data.length == 0) {
            data = readData;
        } else {
            int start = data.length;
            data = Arrays.copyOf(data, start + readData.length);
            System.arraycopy(readData, 0, data, start, readData.length);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (null == data || null == request) {
            super.channelReadComplete(ctx);
            return;
        }

        request.setData(data);
        doRequest(request);

        request = null;
        data = null;

        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        data = null;
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        requestHandler.onClose(ctx);
        super.exceptionCaught(ctx, cause);
    }

    protected void doRequest(Request request) {
        this.requestHandler.doRequest(request);
    }

    protected byte[] read(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        try {
            if (!byteBuf.hasArray()) {
                byte []dataSequence = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(dataSequence);


                return dataSequence;
            }

            return null;
        } finally {
            ReferenceCountUtil.release(byteBuf);
        }
    }
}
