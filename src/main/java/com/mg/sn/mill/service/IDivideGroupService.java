package com.mg.sn.mill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.dto.DivideGroupDto;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.utils.result.StarNodeResultObject;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hcy
 * @since 2019-08-12
 */
public interface IDivideGroupService extends IService<DivideGroup> {

    /**
     * 查看分组信息
     * @param name  名称
     * @param currencyId  币种ID
     * @param currencyName  币种名称
     * @param miningPool  矿池
     * @param walletId  钱包ID
     * @param walletName  钱包名称
     * @param remark  备注
     * @param pageIndex  页数
     * @param pageSize  页大小
     * @return
     */
    StarNodeResultObject queryList (String name, String currencyId, String currencyName, String miningPool, String walletId, String walletName, String remark,
                                    String createUserId, String createUserName, String pageIndex, String pageSize);

    /**
     * 验证分组
     * @param name  ID
     * @param name  名称
     * @param currencyId  币种ID
     * @param walletId  钱包ID
     * @param defaultStatus  默认状态(1:启动;2:未启动)
     * @return
     */
    List<DivideGroup> queryFroVali (String id, String name, String currencyId, String walletId, String defaultStatus);

    /**
     * 保存分组信息
     * @param name  名称
     * @param currencyId  币种ID
     * @param miningPool  矿池
     * @param walletId  钱包ID
     * @param remark  备注
     * @return
     * @throws Exception
     */
    DivideGroup save (String name, String currencyId, String miningPool, String walletId, String remark) throws Exception;

    /**
     * 更新默认状态
     * @param id  分组ID
     * @param defaultStatus  默认状态(1:启动;2:未启动)
     * @return
     */
    DivideGroup updateDefaultStatus (String id, String defaultStatus) throws Exception;

    /**
     * 更新分组信息
     * @param id  ID
     * @param name  名称
     * @param currencyId  币种ID
     * @param walletId  钱包ID
     * @param remark  备注
     * @return
     */
    DivideGroup update (String id, String name, String currencyId, String walletId, String remark) throws Exception;

}
