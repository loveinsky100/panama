package org.leo.server.panama.core.connector;

import org.leo.server.panama.core.method.RequestMethod;

public interface Request {
    RequestMethod requestMethod();
    void close();
    void flush();
    String clientIp();
    int clientPort();
    void write(Response response);
    void writeMsg(Object msg);

    default void setData(byte []data) {
        throw new RuntimeException("unsupport method");
    }

    void setMessage(String message);
}