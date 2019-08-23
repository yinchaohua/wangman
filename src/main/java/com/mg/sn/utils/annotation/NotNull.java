package com.mg.sn.utils.annotation;

import java.lang.annotation.*;

/**
 * 校验参数是否为空
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

    /**
     * 参数
     */
    Param[] value();

}
