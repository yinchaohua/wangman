package com.mg.sn.mill.mapper;

import com.mg.sn.mill.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

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

}
