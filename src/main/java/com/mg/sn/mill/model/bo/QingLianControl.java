package com.mg.sn.mill.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 控制设备
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class QingLianControl {

    /**
     * mac地址
     */
    private String mac;

    /**
     * 功能点
     */
    private String key;

    /**
     * 功能点对应值
     */
    private String value;

    
}
