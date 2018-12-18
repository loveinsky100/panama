package org.leo.server.panama.spring.function.handler.tcp;

import org.leo.server.panama.core.connector.impl.TCPRequest;
import org.leo.server.panama.spring.function.handler.AbstractRequestFunctionHandler;
import org.springframework.stereotype.Service;

@Service("nettyRequestFunctionTcpHandler")
public class NettyRequestFunctionTcpHandler extends AbstractRequestFunctionHandler<TCPRequest> {

    /**
     * 协议如下
     *
     * @param request
     * @return
     */
    @Override
    protected String resolveRequestFunction(TCPRequest request) {
        byte []data = request.getData();
        String message = new String(data);
        request.setMessage(message);
        return request.function();
    }
}
