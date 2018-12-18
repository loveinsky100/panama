package org.leo.server.panama.spring.function.annotation;

import org.leo.server.panama.core.method.RequestMethod;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Controller
public @interface RequestFunction {

    /**
     * 前端请求function
     * @return
     */
    String name() default "";

    /**
     * 请求的类型
     * @return
     */
    RequestMethod method() default RequestMethod.HTTP;
}
