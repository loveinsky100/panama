package org.leo.server.panama.core.handler;

import org.leo.server.panama.core.connector.Request;

public interface RequestHandler<T extends Request> {
    void doRequest(T request);
}