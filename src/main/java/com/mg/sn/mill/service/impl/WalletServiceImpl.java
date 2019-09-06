package com.mg.sn.mill.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mg.sn.mill.controller.WalletController;
import com.mg.sn.mill.mapper.WalletMapper;
import com.mg.sn.mill.model.dto.WalletDto;
import com.mg.sn.mill.model.entity.Wallet;
import com.mg.sn.mill.service.IWalletService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.sn.utils.result.StarNodeResultObject;
import com.mg.sn.utils.result.StarNodeSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements IWalletService {

    @Resource
    private WalletMapper walletMapper;

    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Override
    public StarNodeResultObject queryList(String name, String currencyId, String currencyName, String address, String pageIndex, String pageSize) {
        try {
            Page page = new Page(Long.parseLong(pageIndex), Long.parseLong(pageSize));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("currencyId", currencyId);
            map.put("currencyName", currencyName);
            map.put("address", address);
            List<WalletDto> result = walletMapper.queryList(page, map);
            page.setRecords(result);
             return StarNodeSwitch.dtoSwitch(page);
        } catch (Exception e) {
            log.error("查询钱包信息异常", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Wallet> queryForVali(String name, String currencyId, String address) {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("currencyId", currencyId);
            map.put("address", address);
            List<Wallet> result = walletMapper.queryForVali(map);
            return result;
        } catch (Exception e) {
            log.error("根据名称查询钱包信息异常", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Wallet save(String name, String currencyId, String address, String userId) throws Exception {
        Wallet entity = new Wallet();
        entity.setName(name);
        entity.setAddress(address);
        entity.setCurrencyId(Integer.parseInt(currencyId));
        entity.setCreateDate(LocalDateTime.now());
        entity.setCreateUserId(Integer.parseInt(userId));

        boolean save = this.save(entity);
        if (!save) {
            throw new Exception("添加钱包入库失败");
        }
        return entity;
    }
}
