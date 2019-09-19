package com.xjwx.fognode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 日志配置
 */
@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "logging")
public class logConfig {

    private String path;
}
