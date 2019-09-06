package com.mg.sn.mill.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author hcy
 * @since 2019-08-27
 */
@Data
@TableName("soft_package")
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class SoftPackage extends Model<SoftPackage> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 名称
     */
    @TableField(value = "NAME")
    private String name;
    /**
     * 版本
     */
    @TableField(value = "VERSION")
    private String version;
    /**
     * 格式
     */
    @TableField(value = "FORMAT")
    private String format;

    /**
     * 创建用户ID
     */
    @TableField(value = "CREATE_USER_ID")
    private Integer createUserId;

    /**
     * 创建用户时间
     */
    @TableField(value = "CREATE_DATE")
    private LocalDateTime createDate;

    /**
     * 删除状态
     */
    @TableField(value = "DEL_FLAG")
    private String delFlag;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }



}
