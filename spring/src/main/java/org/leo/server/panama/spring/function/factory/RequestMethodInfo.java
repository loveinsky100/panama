package org.leo.server.panama.spring.function.factory;

import org.leo.server.panama.core.method.RequestMethod;
import org.leo.server.panama.spring.function.service.RequestService;

public class RequestMethodInfo {
    private RequestService requestService;
    private RequestMethod requestMethod;

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public RequestService getRequestService() {
        return requestService;
    }

    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
    }
}
