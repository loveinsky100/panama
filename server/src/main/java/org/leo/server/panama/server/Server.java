package org.leo.server.panama.server;

import java.util.concurrent.Future;

public interface Server {
    default void start() {
        start(1);
    }

    void start(int maxThread);
    int port();
    Future shutdown();
}
