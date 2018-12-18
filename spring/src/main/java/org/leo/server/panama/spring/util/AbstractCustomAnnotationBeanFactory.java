package org.leo.server.panama.spring.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCustomAnnotationBeanFactory<T, E> implements AnnotationBeanFactory, ApplicationListener<ContextRefreshedEvent> {
    private final static Log log = LogFactory.getLog(AbstractCustomAnnotationBeanFactory.class);

    private Map<String, E> customKey2BeanMap;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        customKey2BeanMap = new HashMap<String, E>();
        Map<String, T> beanName2BeanMap = null;
        try {
            Class beanClass = beanClass();
            if (null == beanClass) {
                log.error("beanClass must not be null");
                return;
            }

            beanName2BeanMap = event.getApplicationContext().getBeansOfType(beanClass);
        } catch (Exception e) {
            log.error("beanClass load error : " + e);
        }

        if(null == beanName2BeanMap || beanName2BeanMap.isEmpty()) {
            log.error("beanClass load error, map is empty");
            return;
        }

        for (T bean : beanName2BeanMap.values()) {
            Annotation annotation = getAnnotationForBean(bean);
            String key = getKeyForBean(annotation);
            if (null != key && !key.isEmpty()) {
                E realBean = createBean(bean, annotation);
                customKey2BeanMap.put(key, realBean);
            }
        }
    }

    @Override
    public E getBean(String name) {
        return customKey2BeanMap.get(name);
    }

    protected abstract Class<? extends T> beanClass();
    protected abstract Annotation getAnnotationForBean(T bean);
    protected abstract String getKeyForBean(Annotation annotation);
    protected abstract E createBean(T bean, Annotation annotation);

    @Override
    public String toString() {
        return "AbstractCustomAnnotationBeanFactory{" +
                "customKey2BeanMap=" + customKey2BeanMap +
                '}';
    }
}
