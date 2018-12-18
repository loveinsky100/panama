package org.leo.server.panama.spring.util;

public interface AnnotationBeanFactory<T> {
    T getBean(String name);
}
