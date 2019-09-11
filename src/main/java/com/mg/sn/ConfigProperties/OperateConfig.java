package com.mg.sn.ConfigProperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 矿机操作配置文件
 */

@Data
@Component
@ConfigurationProperties(prefix = "operate")
public class OperateConfig {

    /**
     *  安装
     */
    private String installFile;

    /**
     *  开启
     */
    private String startProcess;

    /**
     *  结束
     */
    private String stopProcess;

    /**
     *  重启进程
     */
    private String restartProcess;

    /**
     *  重启服务
     */
    private String restartServer;

    /**
     *  控制
     */
    private String controlMill;

    /**
     *  矿机列表
     */
    private String millList;
}
