package com.mg.sn.mill.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class DivideGroupDto implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 矿池
     */
    private String miningPool;

    /**
     * 备注
     */
    private String remark;

    /**
     * 默认状态1:启动;2:未启动
     */
    private String defaultStatus;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 最后修改人ID
     */
    private Integer modifyUserId;

    /**
     * 最后修改时间
     */
    private LocalDateTime modifyDate;

    /**
     * 钱包ID
     */
    private Integer walletId;

    /**
     * 币种ID
     */
    private Integer currencyId;

    /**
     * 创建用户名称
     */
    private String createUserName;

    /**
     * 币种名称
     */
    private String currencyName;

    /**
     * 钱包名称
     */
    private String walletName;

    /**
     * 设备数量
     */
    private String equipmentCount;

}
