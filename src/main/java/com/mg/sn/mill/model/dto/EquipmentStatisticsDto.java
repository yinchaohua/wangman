package com.mg.sn.mill.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 设备统计
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class EquipmentStatisticsDto {

    /**
     * 在线数量
     */
    private String eOnlineSum;

    /**
     * 不在线数量
     */
    private String eNotOnlineSum;

    /**
     * 总数
     */
    private String eTotalCount;

    /**
     * 在线率
     */
    private String eOnlineRate;

    /**
     * 未分组数量
     */
    private String eNotGroupSum;
}
