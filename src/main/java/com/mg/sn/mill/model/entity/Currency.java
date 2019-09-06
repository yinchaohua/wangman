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
@TableName("currency")
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class Currency extends Model<Currency> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 币种类型
     */
    @TableField(value = "TYPE")
    private String type;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_DATE")
    private LocalDateTime createDate;
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
