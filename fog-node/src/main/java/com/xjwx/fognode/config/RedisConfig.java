package com.xjwx.fognode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * redis配置
 */
@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    private String host;

    private String port;

    private String timeout;

    private String password;
}
