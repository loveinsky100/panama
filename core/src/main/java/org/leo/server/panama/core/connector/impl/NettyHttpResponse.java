package org.leo.server.panama.core.connector.impl;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class NettyHttpResponse extends HttpResponse {
    private boolean keepAlive;
    private HttpVersion version;
    private HttpResponseStatus status;

    public NettyHttpResponse() {
        super();
        this.version = HttpVersion.HTTP_1_1;
        this.status =  HttpResponseStatus.OK;
    }

    public NettyHttpResponse(String message) {
        super(message);
        this.version = HttpVersion.HTTP_1_1;
        this.status =  HttpResponseStatus.OK;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }
}
