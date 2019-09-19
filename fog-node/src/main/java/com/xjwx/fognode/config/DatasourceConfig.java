package com.xjwx.fognode.config;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 数据库驱动、druid配置
 */
@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceConfig {

    private String username;

    private String password;

    private String url;

    private String driverClassName;

    private String type;

    private String initialSize;

    private String minIdle;

    private String maxActive;

    private String maxWait;

    private String timeBetweenEvictionRunsMillis;

    private String minEvictableIdleTimeMillis;

    private String validationQuery;

    private String testWhileIdle;

    private String testOnBorrow;

    private String testOnReturn;

    private String poolPreparedStatements;

    private String maxPoolPreparedStatementPerConnectionSize;

    private String connectionProperties;

    private String useGlobalDataSourceStat;

}
