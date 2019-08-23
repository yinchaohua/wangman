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
 * @since 2019-08-09
 */
@Data
@TableName("wallet")
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class Wallet extends Model<Wallet> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO, value = "ID")
    private Integer id;
    /**
     * 钱包名称
     */
    @TableField(value = "NAME")
    private String name;
    /**
     * 钱包地址
     */
    @TableField(value = "ADDRESS")
    private String address;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_DATE")
    private LocalDateTime createDate;
    /**
     * 创建人ID
     */
    @TableField(value = "CREATE_USER_ID")
    private Integer createUserId;
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
