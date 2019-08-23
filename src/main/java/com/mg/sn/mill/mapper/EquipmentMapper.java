package com.mg.sn.mill.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mg.sn.mill.model.dto.WalletDto;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.model.entity.Equipment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hcy
 * @since 2019-08-14
 */
@Mapper
@Repository
public interface EquipmentMapper extends BaseMapper<Equipment> {

    /**
     * 验证设备信息
     * @param map
     * @return
     */
    List<Equipment> queryFroVali (HashMap<String, Object> map);

    /**
     * 分页查询设备信息
     * @param page
     * @param map
     * @return
     */
    List<Equipment> queryPage (Page page, Map<String, Object> map);

}
