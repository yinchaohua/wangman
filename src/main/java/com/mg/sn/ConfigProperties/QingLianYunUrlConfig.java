package com.mg.sn.ConfigProperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 青莲云url配置文件
 */

@Data
@Component
@PropertySource({"classpath:application.yml"})
@ConfigurationProperties(prefix = "qinglianyunurl")
public class QingLianYunUrlConfig {

    /**
     *  单个设备详情
     */
    private String singleEquipmentDetails;

    /**
     *  设备在线时长
     */
    private String equipmentOnlineTime;

    /**
     *  批量控制设备
     */
    private String batchControlEquipment;

    /**
     *  最新上报数据
     */
    private String lastReportData;
}
