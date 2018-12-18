package org.leo.server.panama.core.connector.impl;

import org.leo.server.panama.core.connector.Response;

public class TCPResponse implements Response {

    private byte []data;

    public TCPResponse(String msg) {
        this.data = msg.getBytes();
    }

    public TCPResponse(byte []data) {
        this.data = data;
    }

    @Override
    public String getMessage() {
        return new String(data);
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
