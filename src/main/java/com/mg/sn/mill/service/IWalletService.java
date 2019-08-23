package com.mg.sn.mill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.dto.WalletDto;
import com.mg.sn.mill.model.entity.Wallet;
import com.mg.sn.utils.result.StarNodeResultObject;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */
public interface IWalletService extends IService<Wallet> {

    /**
     * 查询钱包list集合
     * @param name  钱包名称
     * @param currencyId  币种ID
     * @param currencyName  币种名称
     * @param address  钱包地址
     * @param pageIndex  页数
     * @param pageSize  页大小
     * @return
     */
    StarNodeResultObject queryList (String name, String currencyId, String currencyName, String address, String pageIndex, String pageSize);

    /**
     * 验证钱包
     * @param name  名称
     * @param currencyId  币种ID
     * @param address  地址
     * @return
     */
    List<Wallet> queryForVali (String name, String currencyId, String address);


    /**
     * 添加钱包信息
     * @param name  钱包名称
     * @param currencyId  币种ID
     * @param address  钱包地址
     * @return
     */
    Wallet save (String name, String currencyId, String address) throws Exception;

}
