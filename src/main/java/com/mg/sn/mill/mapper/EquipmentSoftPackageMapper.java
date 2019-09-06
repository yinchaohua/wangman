package com.mg.sn.mill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mg.sn.mill.model.entity.EquipmentSoftPackage;
import com.mg.sn.mill.model.entity.SoftPackage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  设备软件包Mapper 接口
 * </p>
 *
 * @author hcy
 * @since 2019-08-27
 */
@Mapper
@Repository
public interface EquipmentSoftPackageMapper extends BaseMapper<EquipmentSoftPackage> {

    /**
     * 查询设备软件包关联表
     * @param map
     * @return
     */
    List<EquipmentSoftPackage> query (HashMap<String, Object> map);
}
