package com.czd.annotation;

import java.lang.annotation.*;

/**
 * 只能标注到类上
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    /**
     * 注册controller类
     * @retrun
     */
    String value() default "";
}
