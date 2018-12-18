package org.leo.server.panama.test.server.function.http;

import org.leo.server.panama.core.connector.impl.HttpRequest;
import org.leo.server.panama.core.connector.impl.HttpResponse;
import org.leo.server.panama.spring.function.annotation.RequestFunction;
import org.leo.server.panama.spring.function.service.HttpRequestService;

@RequestFunction(name = "favicon.ico")
public class FaviconHttpRequestMethod implements HttpRequestService {
    @Override
    public HttpResponse execute(HttpRequest request) {
        return new HttpResponse("favicon.ico");
    }
}
