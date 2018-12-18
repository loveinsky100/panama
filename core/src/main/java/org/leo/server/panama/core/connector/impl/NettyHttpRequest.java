package org.leo.server.panama.core.connector.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import org.leo.server.panama.core.connector.Response;
import org.leo.server.panama.core.method.RequestMethod;
import org.leo.server.panama.util.GzipUtils;

import java.util.HashMap;
import java.util.Map;

public class NettyHttpRequest extends NettyRequest implements HttpRequest {
    protected static final byte[] EMPTY = "".getBytes();
    private io.netty.handler.codec.http.HttpRequest request;

    public NettyHttpRequest(io.netty.handler.codec.http.HttpRequest request) {
        this.request = request;
    }

    public NettyHttpRequest(ChannelHandlerContext ctx, io.netty.handler.codec.http.HttpRequest request) {
        super(ctx);
        this.request = request;
    }

    @Override
    public void setMessage(String message) {
        String uri = uri();
        if (uri.startsWith("/")) {
            uri = uri.substring(1, uri.length());
        }

        if (null != uri) {
            if (null != message && message.length() > 0) {
                message = uri + "&" + message;
            } else {
                message = uri;
            }
        }

        super.setMessage(message);
    }

    @Override
    public void write(Response response) {
        FullHttpResponse httpResponse;
        Map<Object, Object> header = new HashMap<Object, Object>();

        String message = response.getMessage();
        if (response instanceof HttpResponse) {
            HttpResponse hRes = (HttpResponse)response;
            if (null != hRes.getHeader() && !hRes.getMessage().isEmpty()) {
                header.putAll(hRes.getHeader());
            }

            if (hRes.isZip()) {
                header.put(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.GZIP);
            }

            header.put(HttpHeaderNames.CONNECTION, hRes.isAlive() ? HttpHeaderValues.KEEP_ALIVE : HttpHeaderValues.CLOSE);
        } else {
            header.put(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            header.put(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }

        byte []data = EMPTY;
        if (null != message && message.length() != 0) {
            data = message.getBytes();
        }

        if (HttpHeaderValues.GZIP.equals(header.get(HttpHeaderNames.CONTENT_ENCODING))) {
            try {
                data = GzipUtils.gzip(data);
            } catch (Exception e) {
                // do nothing
                header.remove(HttpHeaderNames.CONTENT_ENCODING);
            }
        }

        if (response instanceof NettyHttpResponse) {
            NettyHttpResponse nettyHttpResponse = (NettyHttpResponse)response;
            httpResponse = new DefaultFullHttpResponse(
                    nettyHttpResponse.getVersion(), nettyHttpResponse.getStatus(),
                    Unpooled.wrappedBuffer(data)
            );
        } else {
            httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(data)
            );
        }

        header.put(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        for (Map.Entry<Object, Object> entry : header.entrySet()) {
            httpResponse.headers().set(entry.getKey().toString(), entry.getValue());
        }

        writeMsg(httpResponse);
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public HttpMethod method() {
        return request.method();
    }

    @Override
    public io.netty.handler.codec.http.HttpRequest setMethod(HttpMethod method) {
        return request.setMethod(method);
    }

    @Override
    public String getUri() {
        return request.getUri();
    }

    @Override
    public String uri() {
        return request.uri();
    }

    @Override
    public io.netty.handler.codec.http.HttpRequest setUri(String uri) {
        return request.setUri(uri);
    }

    @Override
    public io.netty.handler.codec.http.HttpRequest setProtocolVersion(HttpVersion version) {
        return request.setProtocolVersion(version);
    }

    @Override
    public HttpVersion getProtocolVersion() {
        return request.getProtocolVersion();
    }

    @Override
    public HttpVersion protocolVersion() {
        return request.protocolVersion();
    }

    @Override
    public HttpHeaders headers() {
        return request.headers();
    }

    @Override
    public DecoderResult getDecoderResult() {
        return request.getDecoderResult();
    }

    @Override
    public DecoderResult decoderResult() {
        return request.decoderResult();
    }

    @Override
    public void setDecoderResult(DecoderResult result) {
        request.setDecoderResult(result);
    }

    @Override
    public RequestMethod requestMethod() {
        return RequestMethod.HTTP;
    }

    @Override
    public String toString() {
        return "NettyHttpRequest{" +
                "request=" + request +
                '}';
    }
}
