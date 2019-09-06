package com.mg.sn.ConfigProperties;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 星际无限配置文件
 */
@Data
@Component
@ConfigurationProperties(prefix = "xjwx")
public class XjwxConfig {

    /**
     * 企业UID(唯一)
     */
    private String uid;

    /**
     * 企业验证TOKEN(唯一)
     */
    private String token;
}
