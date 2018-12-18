package org.leo.server.panama.test.server.function.websocket;

import org.leo.server.panama.core.connector.impl.WebSocketRequest;
import org.leo.server.panama.core.connector.impl.WebSocketResponse;
import org.leo.server.panama.core.method.RequestMethod;
import org.leo.server.panama.spring.function.annotation.RequestFunction;
import org.leo.server.panama.spring.function.service.WebSocketRequestService;

@RequestFunction(name = "hello", method = RequestMethod.WS)
public class HelloWorldWebSocketRequestMethod implements WebSocketRequestService {
    @Override
    public WebSocketResponse execute(WebSocketRequest request) {
        WebSocketResponse response = new WebSocketResponse(request.getAttribute("name"));
        return response;
    }
}