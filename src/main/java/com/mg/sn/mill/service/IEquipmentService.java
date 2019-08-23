package com.mg.sn.mill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.model.entity.Equipment;
import com.mg.sn.utils.result.InitEquipmentResult;
import com.mg.sn.utils.result.StarNodeResultObject;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hcy
 * @since 2019-08-14
 */
public interface IEquipmentService extends IService<Equipment> {

    /**
     * 查询设备验证
     * @param groupId
     * @return
     */
    List<Equipment> queryForVali (String groupId)  throws Exception ;

    /**
     * 初始化设备信息
     * @param divideGroup  分组信息
     * @return
     */
    InitEquipmentResult initEquipment(List<DivideGroup> divideGroup);

    /**
     * 分页查询设备信息
     * @param name
     * @param ip
     * @param pageIndex
     * @param pageSize
     * @return
     */
    StarNodeResultObject queryPage (String name, String ip, String pageIndex, String pageSize);

    /**
     * 设置设备分组
     * @param ids
     * @param groupId
     */
    Collection<Equipment> setDivideGroup (String[] ids, String groupId) throws Exception;
}
