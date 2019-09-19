package com.xjwx.fognode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * mybatis-plus配置
 */
@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "mybatis-plus")
public class MybatisPlusConfig {

    private String mapperLocations;

    private String typeAliasesSuperType;
}
