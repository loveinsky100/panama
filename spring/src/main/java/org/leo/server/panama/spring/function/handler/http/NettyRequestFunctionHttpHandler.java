package org.leo.server.panama.spring.function.handler.http;

import org.leo.server.panama.core.connector.impl.HttpRequest;
import org.leo.server.panama.spring.function.handler.AbstractRequestFunctionHandler;
import org.springframework.stereotype.Service;

@Service("nettyRequestFunctionHttpHandler")
public class NettyRequestFunctionHttpHandler extends AbstractRequestFunctionHandler<HttpRequest> {

    @Override
    protected String resolveRequestFunction(HttpRequest request) {
        return request.function();
    }
}
