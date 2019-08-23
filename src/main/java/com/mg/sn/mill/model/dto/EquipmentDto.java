package com.mg.sn.mill.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class EquipmentDto implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 主设备的 iotId
     */
    private String iotId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * Ip地址
     */
    private String ip;

    /**
     * 运行状态1:正常;2:失联;3:未分组;4:删除
     */
    private String runStatus;

    /**
     * 关联用户手机号
     */
    private String contactUserPhone;

    /**
     * 关联用户名称
     */
    private String contactUserName;

    /**
     * 关联用户ID
     */
    private String contactUserId;

    /**
     * 关联用户NO
     */
    private String contactUserNo;

    /**
     * 关联用户昵称
     */
    private String contactUserNickName;

    /**
     * 是否冻结 0:未冻结 1:冻结
     */
    private Integer freeze;

    /**
     * 昨日在线小时
     */
    private BigDecimal ytdOnlineHour;

    /**
     * 昨日在线分钟
     */
    private BigDecimal ytdOnlineMinute;

    /**
     * 累计在线小时
     */
    private BigDecimal totalOnlineHour;

    /**
     * 累计在线分钟
     */
    private BigDecimal totalOnlineMinute;

    /**
     * 分组ID
     */
    private Integer groupId;

    /**
     * mac地址
     */
    private String mac;

    /**
     * 青莲云用户ID
     */
    private String qingLianUserId;

    /**
     * 青莲云用户token
     */
    private String qingLianUserToken;

    /**
     * 设备的数据点数目
     */
    private String dataPointNum;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备显示名称
     */
    private String displayName;

    /**
     * 设备经纬度
     */
    private String ipLocation;

    /**
     * 设备是否联动
     */
    private String isLinkage;

    /**
     * 设备是否在线
     */
    private String isOnline;

    /**
     * 设备的WIFI芯片
     */
    private String wifiType;
}
