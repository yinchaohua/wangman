package com.mg.sn.mill.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 设备在线时长
 */
@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class QingLianOnlineTimeBo implements Serializable {

    //在线:0; 离线:1
    private String event;

    //时间
    private String tt;
}
