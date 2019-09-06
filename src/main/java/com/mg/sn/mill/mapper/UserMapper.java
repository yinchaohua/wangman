package com.mg.sn.mill.mapper;

import com.mg.sn.mill.model.entity.SoftPackage;
import com.mg.sn.mill.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */

@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 登陆验证
     * @param map
     * @return
     */
    List<User> queryForLogin (HashMap<String, Object> map);

}
