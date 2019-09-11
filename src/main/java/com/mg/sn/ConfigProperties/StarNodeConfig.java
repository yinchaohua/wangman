package com.mg.sn.ConfigProperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 星节点配置文件
 */
@Data
@Component
@ConfigurationProperties(prefix = "starnode")
public class StarNodeConfig {

    /**
     * 固定参数
     */
    private String fixedParam;

    /**
     * 调用设备url
     */
    private String equipmentUrl;

    /**
     * 调用用户url
     */
    private String userUrl;

    /**
     * 公钥
     */
    private String publicKey;


}
