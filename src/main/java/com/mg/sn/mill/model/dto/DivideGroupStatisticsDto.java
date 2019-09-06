package com.mg.sn.mill.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分组统计
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class DivideGroupStatisticsDto implements Serializable {

    /**
     * 分组ID
     */
    private Integer groupId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 在线率
     */
    private String onlineRate;

    /**
     * 在线数量
     */
    private String onlineSum;

    /**
     * 不在线数量
     */
    private String notOnlineSum;

    /**
     *  总数
     */
    private String totalCount;


}
