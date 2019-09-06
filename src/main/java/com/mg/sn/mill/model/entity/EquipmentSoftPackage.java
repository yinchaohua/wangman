package com.mg.sn.mill.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2019-08-27
 */
@Data
@TableName("equipment_soft_package")
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class EquipmentSoftPackage extends Model<EquipmentSoftPackage> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 设备ID
     */
    @TableField(value = "EQUIPMENT_ID")
    private Integer equipmentId;
    /**
     * 软件包ID
     */
    @TableField(value = "SOFT_PACKAGE_ID")
    private Integer softPackageId;
    /**
     * 矿机ID
     */
    @TableField(value = "MINING_ID")
    private String miningId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
