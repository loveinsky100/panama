package org.leo.server.panama.spring.function.handler.websocket;

import org.leo.server.panama.core.connector.impl.WebSocketRequest;
import org.leo.server.panama.spring.function.handler.AbstractRequestFunctionHandler;
import org.springframework.stereotype.Service;

@Service("nettyRequestFunctionWebSocketHandler")
public class NettyRequestFunctionWebSocketHandler extends AbstractRequestFunctionHandler<WebSocketRequest> {

    @Override
    protected String resolveRequestFunction(WebSocketRequest request) {
        return request.function();
    }
}
