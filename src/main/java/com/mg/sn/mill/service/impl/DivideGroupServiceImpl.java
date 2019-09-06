package com.mg.sn.mill.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mg.sn.mill.mapper.DivideGroupMapper;
import com.mg.sn.mill.model.dto.DivideGroupDto;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.service.IDivideGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.sn.utils.result.CommonConstant;
import com.mg.sn.utils.result.StarNodeResultObject;
import com.mg.sn.utils.result.StarNodeSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2019-08-12
 */
@Service
public class DivideGroupServiceImpl extends ServiceImpl<DivideGroupMapper, DivideGroup> implements IDivideGroupService {

    @Resource
    private DivideGroupMapper divideGroupMapper;

    private static final Logger log = LoggerFactory.getLogger(DivideGroupServiceImpl.class);

    @Override
    public StarNodeResultObject queryList(String name, String currencyId, String currencyName, String miningPool, String walletId, String walletName, String remark,
                                          String createUserId, String createUserName, String pageIndex, String pageSize) {
        try {
            Page page = new Page(Long.parseLong(pageIndex), Long.parseLong(pageSize));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("currencyId", currencyId);
            map.put("currencyName", currencyName);
            map.put("miningPool", miningPool);
            map.put("walletId", walletId);
            map.put("walletName", walletName);
            map.put("createUserId", createUserId);
            map.put("createUserName", createUserName);
            map.put("remark", remark);
            List<DivideGroupDto> result = divideGroupMapper.queryList(page, map);
            page.setRecords(result);
            return StarNodeSwitch.dtoSwitch(page);
        } catch (Exception e) {
            log.error("查询分组信息异常", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DivideGroup> queryFroVali(String id, String name, String currencyId, String walletId, String defaultStatus) {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            map.put("name", name);
            map.put("currencyId", currencyId);
            map.put("walletId", walletId);
            map.put("defaultStatus", defaultStatus);
            List<DivideGroup> result = divideGroupMapper.queryFroVali(map);
            return result;
        } catch (Exception e) {
            log.error("根据名称查询分组信息异常", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DivideGroup save(String name, String currencyId, String miningPool, String walletId, String remark, String userId) throws Exception {
        DivideGroup entity = new DivideGroup();
        entity.setName(name);
        entity.setMiningPool(miningPool);
        entity.setRemark(remark);
        //默认状态1:启动;2:未启动
        entity.setDefaultStatus(CommonConstant.DEFAULT_STATUS_NOT_START);
        //创建用户ID
        entity.setCreateUserId(Integer.parseInt(userId));
        //创建时间
        entity.setCreateDate(LocalDateTime.now());
        //最后修改人ID
        entity.setModifyUserId(Integer.parseInt(userId));
        //最后修改时间
        entity.setModifyDate(LocalDateTime.now());
        //钱包ID
        entity.setWalletId(Integer.parseInt(walletId));
        //币种ID
        entity.setCurrencyId(Integer.parseInt(currencyId));

        boolean save = this.save(entity);
        if (!save) {
            throw new Exception("添加分组失败");
        }
        return entity;
    }

    @Override
    public DivideGroup updateDefaultStatus(String id, String defaultStatus, String userId)  throws Exception {
        DivideGroup entity = new DivideGroup();
        entity.setId(Integer.parseInt(id));
        entity.setDefaultStatus(defaultStatus);
        //最后修改人
        entity.setModifyUserId(Integer.parseInt(userId));
        //最后修改时间
        entity.setModifyDate(LocalDateTime.now());
        boolean result = this.updateById(entity);
        if (!result) {
            throw new Exception("设置分组默认状态失败");
        }
        return entity;
    }

    @Override
    public DivideGroup update (String id, String name, String currencyId, String walletId, String remark, String userId) throws Exception {
        DivideGroup entity = new DivideGroup();
        entity.setId(Integer.parseInt(id));
        entity.setName(name);
        entity.setCurrencyId(Integer.parseInt(currencyId));
        entity.setWalletId(Integer.parseInt(walletId));
        entity.setRemark(remark);
        //最后修改人
        entity.setModifyUserId(Integer.parseInt(userId));
        //最后修改时间
        entity.setModifyDate(LocalDateTime.now());
        boolean result = this.updateById(entity);
        if (!result) {
            throw new Exception("修改分组信息失败");
        }
        return entity;
    }

}
