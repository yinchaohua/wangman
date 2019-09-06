package com.mg.sn.mill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mg.sn.mill.model.dto.WalletDto;
import com.mg.sn.mill.model.entity.Wallet;
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
 * @since 2019-08-09
 */

@Mapper
@Repository
public interface WalletMapper extends BaseMapper<Wallet> {

    /**
     * 查询钱包信息
     * @param map
     * @return
     */
    List<WalletDto> queryList (Page page, @Param("params") Map<String, Object> map);

    /**
     * 验证钱包信息
     * @param map
     * @return
     */
    List<Wallet> queryForVali (HashMap<String, Object> map);

}
