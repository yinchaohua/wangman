package com.mg.sn.mill.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author hcy
 * @since 2019-08-12
 */
@Data
@TableName("divide_group")
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class DivideGroup extends Model<DivideGroup> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 分组名称
     */
    @TableField(value = "NAME")
    private String name;
    /**
     * 矿池
     */
    @TableField(value = "MINING_POOL")
    private String miningPool;
    /**
     * 备注
     */
    @TableField(value = "REMARK")
    private String remark;
    /**
     * 默认状态1:启动;2:未启动
     */
    @TableField(value = "DEFAULT_STATUS")
    private String defaultStatus;
    /**
     * 创建人ID
     */
    @TableField(value = "CREATE_USER_ID")
    private Integer createUserId;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_DATE")
    private LocalDateTime createDate;
    /**
     * 最后修改人ID
     */
    @TableField(value = "MODIFY_USER_ID")
    private Integer modifyUserId;
    /**
     * 最后修改时间
     */
    @TableField(value = "MODIFY_DATE")
    private LocalDateTime modifyDate;
    /**
     * 钱包ID
     */
    @TableField(value = "WALLET_ID")
    private Integer walletId;
    /**
     * 币种ID
     */
    @TableField(value = "CURRENCY_ID")
    private Integer currencyId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
