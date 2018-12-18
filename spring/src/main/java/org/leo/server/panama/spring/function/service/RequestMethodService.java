package org.leo.server.panama.spring.function.service;

import org.leo.server.panama.core.connector.Request;

import java.util.Map;

public interface RequestMethodService {
    Map<String, Object> execute(String method, Request request);
}
