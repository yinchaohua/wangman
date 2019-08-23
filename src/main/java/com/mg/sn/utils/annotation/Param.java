package com.mg.sn.utils.annotation;

public @interface Param {

    /**
     *  参数
     */
    String paramKey() default "";

    /**
     * 返回信息
     */
    String message() default "";
}
