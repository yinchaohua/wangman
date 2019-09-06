package com.mg.sn.mill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mg.sn.mill.model.entity.SoftPackage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  软件包Mapper 接口
 * </p>
 *
 * @author hcy
 * @since 2019-08-27
 */
@Mapper
@Repository
public interface SoftPackageMapper extends BaseMapper<SoftPackage> {

    /**
     * 查询软件包
     * @param map
     * @return
     */
    List<SoftPackage> query (HashMap<String, Object> map);

}
