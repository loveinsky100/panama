package org.leo.server.panama.core.connector.impl;

import org.leo.server.panama.core.connector.Response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse implements Response {
    private String message;
    private Map<String, Object> header;
    private boolean zip;
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
