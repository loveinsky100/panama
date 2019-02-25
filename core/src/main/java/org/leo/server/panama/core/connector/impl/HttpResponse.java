package org.leo.server.panama.core.connector.impl;

import org.leo.server.panama.core.connector.Response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse implements Response {
    /**
     * 响应信息
     */
    private String message;

    /**
     * 请求的header信息
     */
    private Map<String, Object> header;

    /**
     * 是否是压缩格式
     */
    private boolean zip;

    /**
     * 是否保持长连接
     */
    private boolean alive;

    public HttpResponse() {
    }

    public HttpResponse(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Map<String, Object> getHeader() {
        return header;
    }

    public void addHeader(String key, String value) {
        if (null == key || null == value) {
            return;
        }

        if (null == this.header) {
            this.header = new HashMap<String, Object>();
        } else {
            this.header.put(key, value);
        }
    }

    public boolean isZip() {
        return zip;
    }

    public void setZip(boolean zip) {
        this.zip = zip;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
