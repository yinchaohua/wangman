package com.mg.sn.mill.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.math.BigInteger;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 *
 * </p>
 *
 * @author hcy
 * @since 2019-08-19
 */
@Data
@TableName("equipment")
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class Equipment extends Model<Equipment> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 主设备的 iotId
     */
    @TableField(value = "IOTID")
    private String iotid;
    /**
     * 设备名称
     */
    @TableField(value = "NAME")
    private String name;
    /**
     * Ip地址
     */
    @TableField(value = "IP")
    private String ip;
    /**
     * mac地址
     */
    @TableField(value = "MAC")
    private String mac;
    /**
     * 运行状态1:正常;2:失联;3:未分组;4:删除
     */
    @TableField(value = "RUN_STATUS")
    private String runStatus;
    /**
     * 关联用户手机号
     */
    @TableField(value = "CONTACT_USER_PHONE")
    private BigInteger contactUserPhone;
    /**
     * 关联用户名称
     */
    @TableField(value = "CONTACT_USER_NAME")
    private String contactUserName;
    /**
     * 关联用户ID
     */
    @TableField(value = "CONTACT_USER_ID")
    private Integer contactUserId;
    /**
     * 关联用户编码
     */
    @TableField(value = "CONTACT_USER_NO")
    private String contactUserNo;
    /**
     * 昨日在线小时
     */
    @TableField(value = "YTD_ONLINE_HOUR")
    private BigDecimal ytdOnlineHour;
    /**
     * 昨日在线分钟
     */
    @TableField(value = "YTD_ONLINE_MINUTE")
    private BigDecimal ytdOnlineMinute;
    /**
     * 累计在线小时
     */
    @TableField(value = "TOTAL_ONLINE_HOUR")
    private BigDecimal totalOnlineHour;
    /**
     * 累计在线分钟
     */
    @TableField(value = "TOTAL_ONLINE_MINUTE")
    private BigDecimal totalOnlineMinute;
    /**
     * 分组ID
     */
    @TableField(value = "GROUP_ID")
    private Integer groupId;
    /**
     * 是否冻结 0:未冻结 1:冻结
     */
    @TableField(value = "FREEZE")
    private Integer freeze;
    /**
     * 青莲云用户ID
     */
    @TableField(value = "QINGLIAN_USER_ID")
    private Integer qinglianUserId;
    /**
     * 青莲云用户token
     */
    @TableField(value = "QINGLIAN_USER_TOKEN")
    private String qinglianUserToken;
    /**
     * 设备的数据点数目
     */
    @TableField(value = "DATA_POINT_NUM")
    private Integer dataPointNum;
    /**
     * 设备类型
     */
    @TableField(value = "DEVICE_TYPE")
    private String deviceType;
    /**
     * 设备经纬度
     */
    @TableField(value = "IP_LOCATION")
    private String ipLocation;
    /**
     * 是否在线 true:是;false:不
     */
    @TableField(value = "IS_ONLINE")
    private String isOnline;
    /**
     * 设备是否联动
     */
    @TableField(value = "IS_LINKAGE")
    private String isLinkage;
    /**
     * 设备的WIFI芯片
     */
    @TableField(value = "WIFI_TYPE")
    private String wifiType;

    /**
     * 矿机运行状态
     */
    @TableField(value = "MINING_RUN_STATUS")
    private String miningRunStatus;

    /**
     * 软件包ID
     */
    private Integer softPackageId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
