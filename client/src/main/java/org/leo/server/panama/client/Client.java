package org.leo.server.panama.client;

import java.net.InetSocketAddress;

public interface Client {
    Client connect(InetSocketAddress inetSocketAddress);
    void send(byte []data, int timeout);
    boolean isClose();
    void setClose(boolean close);
    void close();
}