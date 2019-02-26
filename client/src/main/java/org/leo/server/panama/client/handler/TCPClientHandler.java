package org.leo.server.panama.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.leo.server.panama.client.ClientResponseDelegate;
import org.leo.server.panama.client.tcp.TCPClient;
import org.leo.server.panama.core.connector.impl.TCPResponse;

import java.util.Arrays;

public class TCPClientHandler extends ChannelInboundHandlerAdapter {
    private ClientResponseDelegate clientResponseDelegate;
    private TCPClient tcpClient;
    private byte []completeData;

    public TCPClientHandler(TCPClient tcpClient, ClientResponseDelegate clientResponseDelegate) {
        this.clientResponseDelegate = clientResponseDelegate;
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte []readData = read(ctx, msg);
        if (clientResponseDelegate.shouldDoPerResponse()) {
            clientResponseDelegate.doPerResponse(tcpClient, new TCPResponse(readData));
        }

        if (clientResponseDelegate.shouldDoCompleteResponse()) {
            if (null == completeData || completeData.length == 0) {
                completeData = readData;
            } else {
                int start = completeData.length;
                completeData = Arrays.copyOf(completeData, completeData.length + readData.length);
                System.arraycopy(readData, 0, completeData, start, readData.length);
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        try {
            if (null == completeData) {
                super.channelReadComplete(ctx);
                return;
            }

            if (null != clientResponseDelegate) {
                clientResponseDelegate.doCompleteResponse(tcpClient, new TCPResponse(completeData));
            }

            completeData = null;
            super.channelReadComplete(ctx);
        } finally {
            if (null != clientResponseDelegate) {
                clientResponseDelegate.onResponseComplete(tcpClient);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        completeData = null;
        cause.printStackTrace();
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        super.exceptionCaught(ctx, cause);
        clientResponseDelegate.onConnectClosed(tcpClient);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        clientResponseDelegate.onConnectClosed(tcpClient);
    }

    protected byte[] read(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        if (!byteBuf.hasArray()) {
            byte []dataSequence = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(dataSequence);
            return dataSequence;
        }

        return null;
    }
}
