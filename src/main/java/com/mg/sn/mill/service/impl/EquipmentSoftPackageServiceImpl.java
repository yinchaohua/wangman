package com.mg.sn.mill.service.impl;

import com.mg.sn.mill.mapper.EquipmentSoftPackageMapper;
import com.mg.sn.mill.model.entity.EquipmentSoftPackage;
import com.mg.sn.mill.service.IEquipmentSoftPackageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2019-08-27
 */
@Service
public class EquipmentSoftPackageServiceImpl extends ServiceImpl<EquipmentSoftPackageMapper, EquipmentSoftPackage> implements IEquipmentSoftPackageService {

    @Autowired
    private EquipmentSoftPackageMapper equipmentSoftPackageMapper;

    @Override
    public List<EquipmentSoftPackage> save(List<EquipmentSoftPackage> list) throws Exception{
        boolean result = this.saveBatch(list);
        if (!result) {
            throw new ServerException("保存设备和软件包异常");
        }
        return list;
    }

    @Override
    public List<EquipmentSoftPackage> query(String equipmentId, String softPackageId, String miningId) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("equipmentId", equipmentId);
        map.put("softPackageId", softPackageId);
        map.put("miningId", miningId);
        List<EquipmentSoftPackage> result = equipmentSoftPackageMapper.query(map);
        return result;
    }

    @Override
    public List<EquipmentSoftPackage> update(List<EquipmentSoftPackage> list) throws Exception {
        boolean result = this.updateBatchById(list);
        if (!result) {
            throw new ServerException("更新设备软件包异常");
        }
        return list;
    }
}
