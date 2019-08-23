package com.mg.sn.mill.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class WalletDto implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 钱包名称
     */
    private String name;

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 创建人ID
     */
    private Integer createUserId;

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

}
