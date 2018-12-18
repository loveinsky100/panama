package org.leo.server.panama.spring.function.factory;

import org.leo.server.panama.spring.function.service.RequestService;
import org.leo.server.panama.spring.function.annotation.RequestFunction;
import org.leo.server.panama.spring.util.AbstractCustomAnnotationBeanFactory;
import org.springframework.stereotype.Controller;

import java.lang.annotation.Annotation;

@Controller("requestServiceFactory")
public class RequestServiceFactoryImpl extends AbstractCustomAnnotationBeanFactory<RequestService, RequestMethodInfo> {

    @Override
    protected Class<? extends RequestService> beanClass() {
        return RequestService.class;
    }

    @Override
    protected Annotation getAnnotationForBean(RequestService bean) {
        RequestFunction requestFunction = bean.getClass().getAnnotation(RequestFunction.class);
        return requestFunction;
    }

    @Override
    protected String getKeyForBean(Annotation annotation) {
        if (null == annotation || !(annotation instanceof RequestFunction)) {
            return null;
        }

        RequestFunction requestFunction = (RequestFunction)annotation;
        boolean typeNotExist = (null == requestFunction || (null == requestFunction.name() || requestFunction.name().isEmpty()));
        if (typeNotExist) {
            return null;
        }

        String key = requestFunction.method().name() + ":" + requestFunction.name();
        return key;
    }

    @Override
    protected RequestMethodInfo createBean(RequestService requestService, Annotation annotation) {
        if (null == annotation || !(annotation instanceof RequestFunction)) {
            return null;
        }

        if (null == requestService) {
            return null;
        }

        RequestFunction requestFunction = (RequestFunction)annotation;
        RequestMethodInfo requestMethodInfo = new RequestMethodInfo();
        requestMethodInfo.setRequestService(requestService);
        requestMethodInfo.setRequestMethod(requestFunction.method());

        return requestMethodInfo;
    }
}
