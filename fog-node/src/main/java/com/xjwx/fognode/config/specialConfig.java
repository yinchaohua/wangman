package com.xjwx.fognode.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@RefreshScope
@Component
public class specialConfig {

    @Value("${spring.datasource.filters.commons-log.connection-logger-name}")
    private String commonsLogConnectionLoggerName;

    @Value("${spring.redis.pool.max-active}")
    private String maxActive;

    @Value("${spring.redis.pool.min-idle}")
    private String minIdle;

    @Value("${spring.redis.pool.max-idle}")
    private String maxIdle;

    @Value("${spring.redis.pool.max-wait}")
    private String maxWait;

    @Value("${mybatis-plus.configuration.log-impl}")
    private String logImpl;

    @Value("${mybatis-plus.configuration.map-underscore-to-camel-case}")
    private String mapUnderscoreToCamelCase;

    @Value("${mybatis-plus.configuration.cache-enabled}")
    private String cacheEnabled;
    @Value("${mybatis-plus.configuration.lazy-loading-enabled}")
    private String lazyLoadingEnabled;

    @Value("${mybatis-plus.configuration.multiple-result-sets-enabled}")
    private String multipleResultSetsEnabled;

    @Value("${mybatis-plus.configuration.use-generated-keys}")
    private String useGeneratedKeys;

    @Value("${mybatis-plus.configuration.default-statement-timeout}")
    private String defaultStatementTimeout;

    @Value("${mybatis-plus.configuration.default-fetch-size}")
    private String defaultFetchSize;

    @Value("${spring.application.name}")
    private String name;

}
