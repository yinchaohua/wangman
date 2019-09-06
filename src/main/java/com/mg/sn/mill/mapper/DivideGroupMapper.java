package com.mg.sn.mill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mg.sn.mill.model.dto.DivideGroupDto;
import com.mg.sn.mill.model.entity.DivideGroup;
import javafx.scene.control.Pagination;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
 * @since 2019-08-12
 */
@Mapper
@Repository
public interface DivideGroupMapper extends BaseMapper<DivideGroup> {

    /**
     * 验证分组信息
     * @param map
     * @return
     */
    List<DivideGroup> queryFroVali (HashMap<String, Object> map);

    /**
     * 查询分组信息
     * @param page
     * @param map
     * @return
     */
    List<DivideGroupDto> queryList (Page page, @Param("params") Map<String, Object> map);

}
