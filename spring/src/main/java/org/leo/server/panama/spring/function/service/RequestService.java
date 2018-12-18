package org.leo.server.panama.spring.function.service;

import org.leo.server.panama.core.connector.Request;
import org.leo.server.panama.core.connector.Response;

public interface RequestService<T extends Request, E extends Response> {
    E execute(T request);
}
