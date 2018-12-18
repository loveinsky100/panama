package org.leo.server.panama.spring.function.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.leo.server.panama.core.connector.Request;
import org.leo.server.panama.core.connector.Response;
import org.leo.server.panama.core.connector.impl.HttpResponse;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.spring.function.factory.RequestMethodInfo;
import org.leo.server.panama.spring.util.AnnotationBeanFactory;

import javax.annotation.Resource;

public abstract class AbstractRequestFunctionHandler<T extends Request> implements RequestHandler<T> {
    private final static Log log = LogFactory.getLog(AbstractRequestFunctionHandler.class);

    @Resource(name = "requestServiceFactory")
    private AnnotationBeanFactory<RequestMethodInfo> annotationBeanFactory;

    @Override
    public void doRequest(T request) {
        String function = resolveRequestFunction(request);
        String key = request.requestMethod().name() + ":" + (function == null ? "" : function);
        RequestMethodInfo requestMethodInfo = annotationBeanFactory.getBean(key);
        Response response = null;
        if (null == requestMethodInfo) {
            response = new HttpResponse("not support");
        } else {
            response = requestMethodInfo.getRequestService().execute(request);
        }

        if (null != response) {
            request.write(response);
            request.flush();
        }
    }

    protected abstract String resolveRequestFunction(T request);
}
