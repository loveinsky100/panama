package org.leo.server.panama.vpn.reverse.client;

import org.leo.server.panama.client.Client;
import org.leo.server.panama.client.ClientResponseDelegate;
import org.leo.server.panama.core.connector.impl.TCPResponse;
import org.leo.server.panama.util.NumberUtils;
import org.leo.server.panama.vpn.reverse.constant.ReverseConstants;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreServer;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * 反向代理客户端，本质上是一个服务端
 * 两端通过tcp保持通信，ReverseClient在对外ip的机器使用
 * ReverseServer在内网机器使用，ReverseServer不停的发起tcp连接到ReverseClient
 * ReverseClient选择一条连接发送报文，ReverseServer收到后进行响应
 * @author xuyangze
 * @date 2018/11/21 3:17 PM
 */
public class ReverseTCPClient implements Client {
    private ReverseCoreServer reverseCoreServer;
    private ClientResponseDelegate<TCPResponse> delegate;
    private volatile boolean closed = true;
    private static int tag = 10000;

    public ReverseTCPClient(ClientResponseDelegate<TCPResponse> delegate, ReverseCoreServer reverseCoreServer) {
        this.reverseCoreServer = reverseCoreServer;
        this.delegate = delegate;
        tag ++;
    }

    @Override
    public Client connect(InetSocketAddress inetSocketAddress) {
        this.closed = false;
        return this;
    }

    @Override
    public void send(byte[] data, int timeout) {
        if (isClose()) {
            System.out.println("send when closed for tag: " + tag);
            return;
        }

        reverseCoreServer.send2Client(tag, data, responseData -> {
            TCPResponse response = new TCPResponse(responseData);
            delegate.doPerResponse(ReverseTCPClient.this, response);
        }, () -> this.close());
    }

    @Override
    public boolean isClose() {
        return this.closed;
    }

    @Override
    public void setClose(boolean close) {
        this.closed = close;
    }

    @Override
    public void close() {
        if (isClose()) {
            return;
        }

        this.closed = true;
        delegate.onConnectClosed(ReverseTCPClient.this);
        reverseCoreServer.send2Client(tag, NumberUtils.intToByteArray(ReverseConstants.CLOSE_MAGIC), null, null);
    }
}
