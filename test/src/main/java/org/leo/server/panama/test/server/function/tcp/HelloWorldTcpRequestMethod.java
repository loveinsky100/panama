package org.leo.server.panama.test.server.function.tcp;

import org.leo.server.panama.core.connector.impl.TCPRequest;
import org.leo.server.panama.core.connector.impl.TCPResponse;
import org.leo.server.panama.core.method.RequestMethod;
import org.leo.server.panama.spring.function.annotation.RequestFunction;
import org.leo.server.panama.spring.function.service.TCPRequestService;

@RequestFunction(name = "hello", method = RequestMethod.TCP)
public class HelloWorldTcpRequestMethod implements TCPRequestService {
    @Override
    public TCPResponse execute(TCPRequest request) {

        return new TCPResponse("world".getBytes());
    }
}
