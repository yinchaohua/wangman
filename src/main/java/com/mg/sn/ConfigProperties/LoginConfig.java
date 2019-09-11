package com.mg.sn.ConfigProperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 矿机操作配置文件
 */

@Data
@Component
@ConfigurationProperties(prefix = "login")
public class LoginConfig {

    /**
     *  token失效时间
     */
    private long tokenExpireTime;

}
