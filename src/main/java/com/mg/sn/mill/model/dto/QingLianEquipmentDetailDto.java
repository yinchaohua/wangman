package com.mg.sn.mill.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 青莲云接口返回详细信息
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class QingLianEquipmentDetailDto implements Serializable {

    /**
     * 设备的数据点数目
     */
    private String dataPointNum;

    /**
     * 设备IP
     */
    private String deviceIp;

    /**
     * 设备mac地址
     */
    private String deviceMac;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备显示名称
     */
    private String displayName;

    /**
     * 设备的ID
     */
    private String iotId;

    /**
     * 设备经纬度
     */
    private String ipLocation;

    /**
     * 设备是否联动
     */
    private String linkage;

    /**
     * 设备是否在线
     */
    private String online;

    /**
     * 设备的WIFI芯片
     */
    private String wifiType;
}
