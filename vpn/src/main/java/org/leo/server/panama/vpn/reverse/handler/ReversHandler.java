package org.leo.server.panama.vpn.reverse.handler;

import org.leo.server.panama.client.ClientResponseDelegate;
import org.leo.server.panama.client.handler.TCPClientHandler;
import org.leo.server.panama.client.tcp.TCPClient;

/**
 * @author xuyangze
 * @date 2018/11/22 1:46 PM
 */
public class ReversHandler extends TCPClientHandler {
    public ReversHandler(TCPClient tcpClient, ClientResponseDelegate clientResponseDelegate) {
        super(tcpClient, clientResponseDelegate);
    }


}
