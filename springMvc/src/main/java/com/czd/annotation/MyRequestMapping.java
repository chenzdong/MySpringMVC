package com.czd.annotation;

import java.lang.annotation.*;

/**
 * 标注到类和方法上
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    /**
     * 表示访问该方法的url
     * @return
     */
    String value() default  "";
}
