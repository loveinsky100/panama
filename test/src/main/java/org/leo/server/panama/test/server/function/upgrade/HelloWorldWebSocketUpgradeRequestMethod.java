package org.leo.server.panama.test.server.function.upgrade;

import org.leo.server.panama.core.connector.impl.HttpRequest;
import org.leo.server.panama.core.connector.impl.UpgradeResponse;
import org.leo.server.panama.core.method.RequestMethod;
import org.leo.server.panama.spring.function.annotation.RequestFunction;
import org.leo.server.panama.spring.function.service.HttpRequestService;

@RequestFunction(name = "ws", method = RequestMethod.UPGRADE_WS)
public class HelloWorldWebSocketUpgradeRequestMethod implements HttpRequestService {
    @Override
    public UpgradeResponse execute(HttpRequest request) {
        UpgradeResponse response = new UpgradeResponse("connect Leo from ws", request.getAttribute("conn").equals("1"));
        return response;
    }
}