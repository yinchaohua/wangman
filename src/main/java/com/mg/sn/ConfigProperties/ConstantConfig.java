package com.mg.sn.ConfigProperties;

import org.springframework.beans.factory.annotation.Autowired;

public class ConstantConfig {

    @Autowired
    private StarNodeConfig starNodeConfig;

    @Autowired
    private XjwxConfig xjwxConfig;

    @Autowired
    private OperateConfig operateConfig;

    @Autowired
    private QingLianYunUrlConfig qingLianYunUrlConfig;

    private static String a;

//    static {
//        a = starNodeConfig.getFixedParam();
//
//    }

}
