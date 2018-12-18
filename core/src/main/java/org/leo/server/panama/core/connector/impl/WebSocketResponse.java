package org.leo.server.panama.core.connector.impl;

import org.leo.server.panama.core.connector.Response;

public class WebSocketResponse implements Response {
    private String message;

    public WebSocketResponse(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
