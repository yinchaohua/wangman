package com.mg.sn.mill.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 星节点返回设备数据
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class StarNodeEquipmentBo implements Serializable {

    //青莲云返回的ID
    private String iotId;

    //电话号码
    private String mobile;

    //星节点用户ID
    private String userId;

    //设备mac
    private String mac;

}
