package com.mg.sn.mill.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.model.entity.Equipment;
import com.mg.sn.utils.result.InitEquipmentResult;
import com.mg.sn.utils.result.StarNodeResultObject;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  设备
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
    List<Equipment> queryForVali (String groupId) throws Exception;

    /**
     * 初始化设备信息
     * @return
     */
    InitEquipmentResult initEquipment();

    /**
     * 分页查询设备信息
     * @param name
     * @param ip
     * @param type
     * @param pageIndex
     * @param pageSize
     * @return
     */
    StarNodeResultObject queryPage (String name, String ip, String type, String pageIndex, String pageSize);

    /**
     * 设备分组
     * @param equipmentList  设备信息
     * @param groupId  分组ID
     * @return
     * @throws Exception
     */
    InitEquipmentResult setDivideGroup (List<Equipment> equipmentList, String groupId) throws Exception;

    /**
     * 控制矿机
     * @param jsonArray  value
     * @param equipment  设备列表
     */
    boolean controlMining (JSONArray jsonArray, List<Equipment> equipment) throws Exception;

    /**
     * 统计分组
     * @param divideGroupName  分组名称
     * @param pageIndex     页码
     * @param pageSize      页大小
     * @return
     * @throws Exception
     */
    StarNodeResultObject statisticsDivideGroup (String divideGroupName, String pageIndex, String pageSize) throws Exception;

    /**
     * 统计设备
     * @return
     * @throws Exception
     */
    StarNodeResultObject statisticsEquipment () throws Exception;

}
