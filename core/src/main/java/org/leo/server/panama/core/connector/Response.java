package org.leo.server.panama.core.connector;

public interface Response {
    String getMessage();

    default byte []getData() {
        return getMessage().getBytes();
    }
}
