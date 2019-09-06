package com.mg.sn.mill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.entity.EquipmentSoftPackage;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hcy
 * @since 2019-08-27
 */
public interface IEquipmentSoftPackageService extends IService<EquipmentSoftPackage> {

    /**
     * 保存设备软件包关联表
     * @param list
     * @return
     */
    List<EquipmentSoftPackage> save (List<EquipmentSoftPackage> list) throws Exception;

    /**
     * 查询设备软件包关联表
     * @param equipmentId  设备ID
     * @param softPackageId    软件包ID
     * @param miningId  矿机ID
     * @return
     * @throws Exception
     */
    List<EquipmentSoftPackage> query (String equipmentId, String softPackageId, String miningId) throws Exception;

    /**
     * 更新设备软件包关联表
     * @param list  设备软件包集合
     * @return
     */
    List<EquipmentSoftPackage> update (List<EquipmentSoftPackage> list) throws Exception;

}
