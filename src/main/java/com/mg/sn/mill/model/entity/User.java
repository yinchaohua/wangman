package com.mg.sn.mill.model.entity;

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
@TableName("user")
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("ID")
    private Integer id;
    /**
     * 用户名
     */
    @TableField(value = "NAME")
    private String name;
    /**
     * 密码
     */
    @TableField(value = "PASSWORD")
    private String password;
    /**
     * 性别
     */
    @TableField(value = "SEX")
    private String sex;
    /**
     * 手机号
     */
    @TableField(value = "PHONE")
    private Integer phone;
    /**
     * 邮箱号
     */
    @TableField(value = "E_MAIL")
    private String eMail;
    /**
     * 证件类型:1 身份证，2: 军官证，3 :护照
     */
    @TableField(value = "IDENTITY_CARD_TYPE")
    private String identityCardType;
    /**
     * 证件号码
     */
    @TableField(value = "IDENTITY_CARD_NO")
    private String identityCardNo;
    /**
     * 注册时间
     */
    @TableField(value = "REGISTER_DATE")
    private LocalDateTime registerDate;
    /**
     * 登陆时间
     */
    @TableField(value = "LOGIN_DATE")
    private LocalDateTime loginDate;
    /**
     * 最后修改时间
     */
    @TableField(value = "MODIFY_DATE")
    private LocalDateTime modifyDate;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
