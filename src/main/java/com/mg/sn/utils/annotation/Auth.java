package com.mg.sn.utils.annotation;

import java.lang.annotation.*;

/**
 * 登陆token校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {

    /**
     *  参数
     */
    String value() default "";

}
