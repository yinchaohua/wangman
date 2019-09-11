package com.mg.sn.mill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.sn.ConfigProperties.OperateConfig;
import com.mg.sn.ConfigProperties.QingLianYunUrlConfig;
import com.mg.sn.ConfigProperties.StarNodeConfig;
import com.mg.sn.ConfigProperties.XjwxConfig;
import com.mg.sn.mill.mapper.DivideGroupMapper;
import com.mg.sn.mill.mapper.EquipmentMapper;
import com.mg.sn.mill.model.bo.*;
import com.mg.sn.mill.model.dto.DivideGroupStatisticsDto;
import com.mg.sn.mill.model.dto.EquipmentStatisticsDto;
import com.mg.sn.mill.model.dto.QingLianEquipmentDetailDto;
import com.mg.sn.mill.model.entity.*;
import com.mg.sn.mill.model.entity.Currency;
import com.mg.sn.mill.service.*;
import com.mg.sn.utils.Enum.DefaultStatusEnum;
import com.mg.sn.utils.Enum.DelFlag;
import com.mg.sn.utils.Enum.MiningRunStatus;
import com.mg.sn.utils.Enum.RunStatus;
import com.mg.sn.utils.common.StringUtils;
import com.mg.sn.utils.common.TimeUtils;
import com.mg.sn.utils.common.TypeConvert;
import com.mg.sn.utils.httpclient.HttpClientResult;
import com.mg.sn.utils.httpclient.HttpClientService;
import com.mg.sn.utils.redis.RedisUtil;
import com.mg.sn.utils.result.InitEquipmentResult;
import com.mg.sn.utils.result.StarNodeResultObject;
import com.mg.sn.utils.result.StarNodeSwitch;
import com.mg.sn.utils.security.SecurityUtils;
import org.apache.http.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.rmi.ServerException;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.*;

import static com.mg.sn.utils.security.SecurityUtils.encrypt;
import static com.mg.sn.utils.security.SecurityUtils.loadPublicKey;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2019-08-14
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements IEquipmentService {
	
	private final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    @Autowired
    private StarNodeConfig starNodeConfig;

    @Autowired
    private XjwxConfig xjwxConfig;

    @Autowired
    private OperateConfig operateConfig;

    @Autowired
    private QingLianYunUrlConfig qingLianYunUrlConfig;

	@Autowired
	private EquipmentMapper equipmentMapper;

    @Resource
    private DivideGroupMapper divideGroupMapper;

    @Autowired
    private IDivideGroupService divideGroupService;

    @Autowired
    private ICurrencyService currencyService;

    @Autowired
    private ISoftPackageService softPackageService;

    @Autowired
    private IEquipmentSoftPackageService equipmentSoftPackageService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Equipment> queryForVali(String groupId)  throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("groupId", groupId);
        List<Equipment> result = equipmentMapper.queryFroVali(map);
        return result;

    }

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public InitEquipmentResult initEquipment() {

        //返回对象
        InitEquipmentResult initEquipmentResult = new InitEquipmentResult();

        //调用星节点接口

        //获取星节点头信息
        HashMap<String, Object> starNodeHeader = null;
        try {
            starNodeHeader = getStarNodeHeader();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化设备失败, 生成星节点头信息 {} 失败", starNodeHeader);
            initEquipmentResult.setCode("01");
            initEquipmentResult.setMessage("初始化设备失败, 生成星节点头信息失败");
            return initEquipmentResult;
        }

        //获取星节点设备信息
        InitEquipmentResult starNodeEquipmentResult = invokeStarNode(starNodeConfig.getEquipmentUrl(), starNodeHeader, initEquipmentResult);
        //获取设备失败
        if (!starNodeEquipmentResult.isSucc()) {
            starNodeEquipmentResult.setMessage("调用星节点设备接口失败, " + starNodeEquipmentResult.getMessage());
            return starNodeEquipmentResult;
        }
        //获取设备信息
        JSONArray starNodeEquipmentJsonArray = (JSONArray) starNodeEquipmentResult.getObj();
        List<StarNodeEquipmentBo> starNodeEquipmentBoList = JSONObject.parseArray(starNodeEquipmentJsonArray.toJSONString(), StarNodeEquipmentBo.class);

        //获取星节点用户信息
        InitEquipmentResult StarNodeUserResult = invokeStarNode(starNodeConfig.getUserUrl(), starNodeHeader, initEquipmentResult);
        //获取设备失败
        if (!StarNodeUserResult.isSucc()) {
            starNodeEquipmentResult.setMessage("调用星节点用户接口失败, " + starNodeEquipmentResult.getMessage());
            return StarNodeUserResult;
        }

        //获取用户信息
        JSONArray starNodeUserJsonArray = (JSONArray) StarNodeUserResult.getObj();
        List<StarNodeUserBo> starNodeUserBoList = JSONObject.parseArray(starNodeUserJsonArray.toJSONString(), StarNodeUserBo.class);

        //更新矿机列表

        //整合设备和用户信息
        ArrayList<Equipment> starNodeEquipmentList = new ArrayList<Equipment>();
        for (int i = 0; i < starNodeEquipmentBoList.size(); i++) {
            Equipment equipment = new Equipment();
            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(starNodeEquipmentBoList.get(i).getUserId())) {
                continue;
            }
            for (int j = 0; j < starNodeUserBoList.size(); j++) {
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(starNodeUserBoList.get(j).getUserId())) {
                    continue;
                }
                if (StringUtils.stringEquals(starNodeEquipmentBoList.get(i).getUserId(), starNodeUserBoList.get(j).getUserId())) {
                    //青莲云返回的ID
                    equipment.setIotid(starNodeEquipmentBoList.get(i).getIotId());
                    //星节点用户电话号码
                    equipment.setContactUserPhone(new BigInteger(starNodeEquipmentBoList.get(i).getMobile()));
                    //星节点用户ID
                    equipment.setContactUserId(Integer.parseInt(starNodeEquipmentBoList.get(i).getUserId()));
                    //mac地址
                    equipment.setMac(starNodeEquipmentBoList.get(i).getMac());
                    ////是否冻结 0:未冻结 1:冻结
                    equipment.setFreeze(starNodeUserBoList.get(j).getFreeze());
                    //星节点用户编码
                    equipment.setContactUserNo(starNodeUserBoList.get(j).getUserNo());
                    //星节点用户昵称
                    equipment.setContactUserName(starNodeUserBoList.get(j).getNickname());
                    //青莲用户ID
                    equipment.setQinglianUserId(Integer.parseInt(starNodeUserBoList.get(j).getQingLianUserId()));
                    //青莲云用户TOKEN
                    equipment.setQinglianUserToken(starNodeUserBoList.get(j).getQingLianUserToken());
                    break;
                }
            }
            if (equipment != null) {
                starNodeEquipmentList.add(equipment);
            }
        }

        //调用青莲云接口

        //调用青莲云接口获取设备详细信息、
        //移除设备list
        ArrayList<Equipment> removeList = new ArrayList<Equipment>();
        //失联设备list
        ArrayList<Equipment> lostInList = new ArrayList<Equipment>();
        for (Equipment starNodeEquipment : starNodeEquipmentList) {
            //获取当前时间戳
            long currentTime = TimeUtils.getCurrentTime();
            //生成32位大写MD5秘钥(uid + token + tt)
            String sign = getQingLianSign(xjwxConfig.getUid(), xjwxConfig.getToken(), currentTime);
            Integer qingLianUserId = starNodeEquipment.getQinglianUserId();
            String qingLianUserToken = starNodeEquipment.getQinglianUserToken();
            String mac = starNodeEquipment.getMac();

            HashMap<String, Object> qingLianMap = new HashMap<>();
            //用户ID
            qingLianMap.put("uid", xjwxConfig.getUid());
            //当前时间戳
            qingLianMap.put("tt", currentTime);
            //用户签名(30位)
            qingLianMap.put("sign", sign);
            //用户唯一标识ID
            qingLianMap.put("userId", qingLianUserId);
            //青莲云用户唯一TOKEN
            qingLianMap.put("userToken", qingLianUserToken);
            //设备MAC地址
            qingLianMap.put("mac", mac);

            //获取参数对象
            List<NameValuePair> qingLianBody = HttpClientService.getParams(qingLianMap);qingLianBody.indexOf(qingLianBody);

            //获取单个设备详细信息
            HttpClientResult qingLianResult = HttpClientService.sendPost(qingLianYunUrlConfig.getSingleEquipmentDetails(), null, qingLianBody, "https");

            InitEquipmentResult convertResult = qingLianTypeConvert(qingLianResult, "singleinfo", initEquipmentResult);
            if (!convertResult.isSucc()) {
                log.error(convertResult.getMessage());
                removeList.add(starNodeEquipment);
//                return convertResult;
                continue;
            }
            //转换成功
            JSON equipmentDetailJson = (JSON) convertResult.getObj();

            QingLianEquipmentDetailDto qingLianEquipmentDetailDto = JSONObject.toJavaObject(equipmentDetailJson, QingLianEquipmentDetailDto.class);

            HashMap<String, Object> onlineTimeMap = new HashMap<>();
            onlineTimeMap.put("mac", mac);
            onlineTimeMap.put("date", TimeUtils.getDate(null));
            onlineTimeMap.put("uid", xjwxConfig.getUid());
            onlineTimeMap.put("tt", currentTime);
            onlineTimeMap.put("sign", sign);

            //获取参数对象
            List<NameValuePair> onlineTimeBody = HttpClientService.getParams(onlineTimeMap);

            //获取设备在线时间
            HttpClientResult onlineTimeResult = HttpClientService.sendPost(qingLianYunUrlConfig.getEquipmentOnlineTime(), null, onlineTimeBody, "https");
            if (onlineTimeResult == null) {
                log.error("获取设备信息失败, 获取设备在线时长信息为空, 参数 {} ", onlineTimeBody);
                continue;
            }

//            InitEquipmentResult onlineConvertResult = qingLianTypeConvert(onlineTimeResult, "data", initEquipmentResult);
//            if (!onlineConvertResult.isSucc()) {
////                return onlineConvertResult;
//                continue;
//            }
//            //转换成功
//            Object data = onlineConvertResult.getObj();
//            if (data == null) {
//
//            }
//            List<QingLianOnlineTimeBo> qingLianOnlineTime = JSONObject.parseArray(onlineTimeJson.toString(), QingLianOnlineTimeBo.class);

            //设备的数据点数目
            starNodeEquipment.setDataPointNum(Integer.parseInt(qingLianEquipmentDetailDto.getDataPointNum()));
            //设备IP
            starNodeEquipment.setIp(qingLianEquipmentDetailDto.getDeviceIp());
            //设备类型
            starNodeEquipment.setDeviceType(qingLianEquipmentDetailDto.getDeviceType());
            //设备显示名称
            starNodeEquipment.setName(qingLianEquipmentDetailDto.getDisplayName());
            //设备经纬度
            starNodeEquipment.setIpLocation(qingLianEquipmentDetailDto.getIpLocation());
            //设备是否联动
            starNodeEquipment.setIsLinkage(qingLianEquipmentDetailDto.getLinkage());
            //设备是否在线
            starNodeEquipment.setIsOnline(qingLianEquipmentDetailDto.getOnline());
            //根据在线状态判断运行状态
            if (StringUtils.stringEquals(qingLianEquipmentDetailDto.getOnline(), "true")) {
                starNodeEquipment.setRunStatus(RunStatus.NORMAL.getCode());
            } else {
                starNodeEquipment.setRunStatus(RunStatus.MISS.getCode());
            }
            //设备的 wifi 芯片
            starNodeEquipment.setWifiType(qingLianEquipmentDetailDto.getWifiType());
            if (StringUtils.stringEquals(starNodeEquipment.getIsOnline(), "false")) {
                lostInList.add(starNodeEquipment);
            }
        }

        //移除报错数据
//        starNodeEquipmentList.removeAll(removeList);
        //TODO 失联设备不参与分组

        //新增的设备信息
        ArrayList<Equipment> addEquipment = new ArrayList<Equipment>();
        ArrayList<Equipment> updateEquipment = new ArrayList<Equipment>();
        List<Equipment> oldDataList = this.list();
//        redisUtil.set("EQUIPMENT_LIST_KEY", oldDataList, 3000);
//        List<Equipment> equipment_list_key = (List<Equipment>)redisUtil.get("EQUIPMENT_LIST_KEY");

        //初始化(表中不存在数据)
        if (oldDataList.size() == 0) {
            boolean result = this.saveBatch(starNodeEquipmentList);
            if (!result) {
                log.error("初始化设备信息失败, 保存设备信息失败 {}", starNodeEquipmentList);
                initEquipmentResult.setCode("14");
                initEquipmentResult.setMessage("初始化设备信息失败, 保存设备信息失败");
                return initEquipmentResult;
            }
            //获取所有设备信息
            List<Equipment> list = this.list();

            //添加默认分组
            InitEquipmentResult EquipmentResult = addDefaultGroup(list, initEquipmentResult);
            if (!EquipmentResult.isSucc()) {
                log.error("初始化设备失败, 新添加设备数据 (表中数据为空) 默认分组失败 list: {}", list);
                initEquipmentResult.setCode("16");
                initEquipmentResult.setMessage("初始化设备信息失败, 新添加设备数据 (表中数据为空) 默认分组失败");
                return initEquipmentResult;
            }

        } else {    //存在数据：更新旧数据; 插入新数据
            HashMap<String, Equipment> stringEquipmentHashMap = new HashMap<String, Equipment>(oldDataList.size());
            //key为mac(唯一), value为原来设备信息
            for (Equipment oldEquipment : oldDataList) {
                stringEquipmentHashMap.put(oldEquipment.getMac(), oldEquipment);
            }
            //判断是否存在设备信息
            for (Equipment newEquipment : starNodeEquipmentList) {
                if (stringEquipmentHashMap.get(newEquipment.getMac()) == null) {
                    addEquipment.add(newEquipment);
                } else {
                    //数据库设备信息
                    Equipment oldEquipment = stringEquipmentHashMap.get(newEquipment.getMac());
                    //运行状态
                    oldEquipment.setRunStatus(newEquipment.getRunStatus());
                    //是否冻结 0:未冻结 1:冻结
                    oldEquipment.setFreeze(newEquipment.getFreeze());
                    //设备的数据点数目
                    oldEquipment.setDataPointNum(newEquipment.getDataPointNum());
                    //设备是否在线
                    oldEquipment.setIsOnline(newEquipment.getIsOnline());
                    //设备是否联动
                    oldEquipment.setIsLinkage(newEquipment.getIsLinkage());
                    //昨日在线小时
                    oldEquipment.setYtdOnlineHour(newEquipment.getYtdOnlineHour());
                    //昨日在线分钟
                    oldEquipment.setYtdOnlineMinute(newEquipment.getTotalOnlineMinute());
                    //累计在线小时
                    oldEquipment.setTotalOnlineHour(newEquipment.getTotalOnlineHour());
                    //累计在线分钟
                    oldEquipment.setTotalOnlineMinute(newEquipment.getYtdOnlineMinute());
                    updateEquipment.add(oldEquipment);
                }
            }

            //更新已经存在设备数据(旧数据更新挖矿状态)
            if (updateEquipment.size() != 0) {
                //获取矿机列表
                JSONArray objectArray = new JSONArray();
                for (Equipment equipment : updateEquipment) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("mac", equipment.getMac());
                    jsonObject.put("key", operateConfig.getMillList());
                    jsonObject.put("value", "矿机列表");
                    objectArray.add(jsonObject);
                }
                HttpClientResult httpClientResult = executeKey(objectArray);
                JSONObject object = httpClientResult.getObject();
                QingLianBo qingLianBo = JSON.parseObject(object.toString(), QingLianBo.class);
                if (!httpClientResult.isSuccess() || !qingLianBo.isSucc()) {
                    log.error("初始化设备失败, 更新旧数据时调用矿机列表失败");
                    initEquipmentResult.setCode("19");
                    initEquipmentResult.setMessage("初始化设备信息失败, 更新旧数据时调用矿机列表失败");
                    return initEquipmentResult;
                }

                ArrayList<EquipmentSoftPackage> updateEquipmentSoftPackageList = new ArrayList<EquipmentSoftPackage>();
                //获取最新上报数据
                for (Equipment equipment : updateEquipment) {
                    if (equipment.getGroupId() != null && equipment.getSoftPackageId() != null) {
                        HttpClientResult lastReportDataResult = lastReportData(equipment.getMac());
//                        HttpClientResult lastReportDataResult = lastReportData("14:6b:9c:f6:03:f8");
                        JSONObject lastReportDataObject = lastReportDataResult.getObject();
                        QingLianBo lastReportDataQingLianBo = JSON.parseObject(lastReportDataObject.toString(), QingLianBo.class);
                        if (!lastReportDataResult.isSuccess() || !lastReportDataQingLianBo.isSucc()) {
                            log.error("初始化设备失败, 更新旧数据时获取最新上报数据失败");
                            initEquipmentResult.setCode("20");
                            initEquipmentResult.setMessage("初始化设备信息失败, 更新旧数据时获取最新上报数据失败");
                            return initEquipmentResult;
                        }
                        String data = lastReportDataQingLianBo.getData();
                        JSONObject jsonObject = JSONObject.parseObject(data);
                        String mill_list = jsonObject.get("mill_list").toString();
                        //矿机列表为空不更新
                        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(mill_list)) {
                            String substring = mill_list.substring(1, mill_list.length() - 1);
                            String[] split = substring.split(",");
                            String fileName = split[0];
                            String version = split[1];
                            String miningRunstatus = split[2].substring(0, 1);
                            String miningId = split[3];
                            //设置矿机运行状态
                            equipment.setMiningRunStatus(miningRunstatus);
                            List<EquipmentSoftPackage> equipmentSoftPackageList;
                            try {
                                equipmentSoftPackageList = equipmentSoftPackageService.query(String.valueOf(equipment.getId()), String.valueOf(equipment.getSoftPackageId()), "");
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error("初始化设备失败, 更新旧设备时获取设备和软件包关联信息失败");
                                initEquipmentResult.setCode("21");
                                initEquipmentResult.setMessage("初始化设备信息失败, 更新旧设备时获取设备和软件包关联信息失败");
                                return initEquipmentResult;
                            }
                            if (equipmentSoftPackageList.size() == 0) {
                                log.error("初始化设备失败, 更新旧设备时设备和软件包关联信息不存在");
                                initEquipmentResult.setCode("22");
                                initEquipmentResult.setMessage("初始化设备信息失败, 更新旧设备时设备和软件包关联信息不存在");
                                return initEquipmentResult;
                            }
                            EquipmentSoftPackage equipmentSoftPackage = equipmentSoftPackageList.get(0);
                            //设置矿机ID
                            equipmentSoftPackage.setMiningId(miningId);
                            updateEquipmentSoftPackageList.add(equipmentSoftPackage);
                        }
                    }
                }

                //更新设备和软件包
                if (updateEquipmentSoftPackageList != null) {
                    try {
                        List<EquipmentSoftPackage> update = equipmentSoftPackageService.update(updateEquipmentSoftPackageList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        initEquipmentResult.setCode("23");
                        initEquipmentResult.setMessage("初始化设备信息失败, 更新旧设备时设备和软件包关联信息不存在");
                        return initEquipmentResult;
                    }
                }


                boolean updateResult = this.updateBatchById(updateEquipment);
                if (!updateResult) {
                    log.error("初始化设备失败, 已存在数据: {} 更新失败", updateEquipment);
                    initEquipmentResult.setCode("24");
                    initEquipmentResult.setMessage("初始化设备信息失败, 已存在数据更新失败");
                    return initEquipmentResult;
                }
            }

            //添加新设备数据(安装矿机文件夹)
            if (addEquipment.size() != 0) {
                boolean addResult = this.saveBatch(addEquipment);
                if (!addResult) {
                    log.error("初始化设备失败, 新添加设备数据: {} 插入失败", addEquipment);
                    initEquipmentResult.setCode("25");
                    initEquipmentResult.setMessage("初始化设备信息失败, 新添加设备数据插入失败");
                    return initEquipmentResult;
                }

                //添加默认分组
                InitEquipmentResult initAddEquipmentResult = addDefaultGroup(addEquipment, initEquipmentResult);
                if (!initAddEquipmentResult.isSucc()) {
                    log.error("初始化设备失败, 新添加设备数据默认分组失败 list: {}", addEquipment);
                    initEquipmentResult.setCode("26");
                    initEquipmentResult.setMessage("初始化设备信息失败, 新添加设备数据默认分组失败");
                    return initEquipmentResult;
                }
            }
        }
        log.error("初始化设备成功");
        initEquipmentResult.setCode("00");
        initEquipmentResult.setMessage("初始化设备成功");
        return initEquipmentResult;
    }

    @Override
    public StarNodeResultObject queryPage(String name, String ip, String type, String userName, String pageIndex, String pageSize) {
        try {
            Page page = new Page(Long.parseLong(pageIndex), Long.parseLong(pageSize));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("ip", ip);
            map.put("userName", userName);
            //正常
            if (StringUtils.isNotEmpty(type)) {
                if (StringUtils.stringEquals(type, RunStatus.NORMAL.getCode())) {
                    map.put("runStatus", type);
                } if (StringUtils.stringEquals(type, RunStatus.MISS.getCode())) {  //失联
                    map.put("runStatus", type);
                } if (StringUtils.stringEquals(type, RunStatus.NO_GROUP.getCode())) {  //未分组
                    map.put("notGroup", type);
                }
            }
            List<Equipment> result = equipmentMapper.queryPage(page, map);
            page.setRecords(result);
            return StarNodeSwitch.dtoSwitch(page);
        } catch (Exception e) {
            log.error("查询设备信息异常", e);
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public InitEquipmentResult setDivideGroup(List<Equipment> equipmentList, String groupId) throws Exception {

        InitEquipmentResult initEquipmentResult = new InitEquipmentResult();
        //批量控制矿机value
        JSONArray jsonArray = new JSONArray();
        //需要添加设备软件包
        ArrayList<EquipmentSoftPackage> addEquipmentSoftPackageList = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            //获取分组信息
            DivideGroup divideGroupId = divideGroupService.getById(groupId);
            //获取币种ID
            Currency currencyEntity = currencyService.getById(divideGroupId.getCurrencyId());
            //获取软件包
            List<SoftPackage> softPackageEntity;
            try {
                softPackageEntity = softPackageService.query(currencyEntity.getType(), "", "", DelFlag.NOT_DEL.getCode());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("设备分组失败, 获取软件包失败");
                initEquipmentResult.setCode("1");
                initEquipmentResult.setMessage("设备分组失败, 获取软件包失败");
                return initEquipmentResult;
            }
            if (softPackageEntity == null) {
                log.error("设备分组失败, 软件包数据为空");
                initEquipmentResult.setCode("2");
                initEquipmentResult.setMessage("设备分组失败, 软件包数据为空");
                return initEquipmentResult;
            }

            //软件包
            SoftPackage softPackage = softPackageEntity.get(0);
            List<EquipmentSoftPackage> equipmentSoftPackageList = equipmentSoftPackageService.query(String.valueOf(equipment.getId()), String.valueOf(softPackage.getId()), "");
            //设备和软件包关联表不存在，需要重新下载
            JSONObject jsonObject = new JSONObject();

            if (equipmentSoftPackageList.size() == 0) {
                String value = "name:" + softPackage.getName() + " version:" + softPackage.getVersion() + " operate:" + operateConfig.getInstallFile();
                jsonObject.put("mac", equipment.getMac());
                jsonObject.put("key", operateConfig.getControlMill());
                jsonObject.put("value", value);

                //添加设备软件包
                EquipmentSoftPackage equipmentSoftPackage = new EquipmentSoftPackage();
                equipmentSoftPackage.setEquipmentId(equipment.getId());
                equipmentSoftPackage.setSoftPackageId(softPackage.getId());
                addEquipmentSoftPackageList.add(equipmentSoftPackage);

            } else {    //存在，启动进程
                String value = "name:" + softPackage.getName() + " version:" + softPackage.getVersion() + " operate:" + operateConfig.getStartProcess();
                jsonObject.put("mac", equipment.getMac());
                jsonObject.put("key", operateConfig.getControlMill());
                jsonObject.put("value", value);
            }
            jsonArray.add(jsonObject);

            //更新分组和软件包ID
            equipment.setGroupId(Integer.parseInt(groupId));
            equipment.setSoftPackageId(softPackage.getId());
            //矿机运行状态:执行中
            equipment.setMiningRunStatus(MiningRunStatus.EXECUTE_MIDDLE.getCode());
        }

        //控制矿机
        HttpClientResult httpClientResult = executeKey(jsonArray);
        JSONObject equipmentObject = httpClientResult.getObject();
        StarNodeBo starNodeEquipmentBo = JSON.parseObject(equipmentObject.toString(), StarNodeBo.class);
        if (!httpClientResult.isSuccess() || !starNodeEquipmentBo.isSucc()) {
            log.error("设备分组失败, 执行矿机失败 msg: {} ", starNodeEquipmentBo.getErrmsg());
            initEquipmentResult.setCode("3");
            initEquipmentResult.setMessage("获取设备信息失败, 执行矿机失败 msg: " + starNodeEquipmentBo.getErrmsg());
            return initEquipmentResult;
        }

        //更新设备信息
        boolean updateEquipment = this.updateBatchById(equipmentList);
        if (updateEquipment) {
            throw new ServerException("设备分组失败, 控制矿机成功更新设备信息失败");
        }

        //添加设备软件包表
        List<EquipmentSoftPackage> saveResult = equipmentSoftPackageService.save(addEquipmentSoftPackageList);

        initEquipmentResult.setCode("00");
        initEquipmentResult.setMessage("设置设备分组成功");
        return initEquipmentResult;
    }

    @Override
    public boolean controlMining (JSONArray jsonArray, List<Equipment> equipment) throws Exception{


        HttpClientResult httpClientResult = executeKey(jsonArray);
        JSONObject equipmentObject = httpClientResult.getObject();
        StarNodeBo starNodeEquipmentBo = JSON.parseObject(equipmentObject.toString(), StarNodeBo.class);
        if (!httpClientResult.isSuccess() || !starNodeEquipmentBo.isSucc()) {
            log.error("控制矿机失败, ", starNodeEquipmentBo.getErrmsg());
            throw new ServerException("控制矿机失败, 执行矿机命令失败");
        }

        //修改所有状态为执行中
        boolean updateResult = this.updateBatchById(equipment);
        if (!updateResult) {
            log.error("控制矿机成功, 修改矿机状态失败");
            throw new ServerException("控制矿机成功, 修改矿机状态失败");
        }
        return updateResult;
    }

    @Override
    public StarNodeResultObject statisticsDivideGroup(String divideGroupName, String pageIndex, String pageSize) throws Exception {
        Page page = new Page(Long.parseLong(pageIndex), Long.parseLong(pageSize));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("divideGroupName", divideGroupName);
        List<DivideGroupStatisticsDto> result = equipmentMapper.statisticsDivideGroup(page, map);
        page.setRecords(result);
        return StarNodeSwitch.dtoSwitch(page);
    }

    @Override
    public StarNodeResultObject statisticsEquipment() throws Exception {
        List<EquipmentStatisticsDto> equipmentStatisticsDtos = equipmentMapper.statisticsEquipment();
        return StarNodeSwitch.dtoSwitch(equipmentStatisticsDtos);
    }

    /**
     * 获取星节点头信息
     * @return
     */
    public HashMap<String, Object> getStarNodeHeader () throws Exception {

        //获取当前时间戳
        long currentTime = TimeUtils.getCurrentTime();
        //加载公钥
        RSAPublicKey rsaPublicKey = loadPublicKey(starNodeConfig.getPublicKey());
        //星节点传入sign
        String sign = encrypt(rsaPublicKey, (currentTime + starNodeConfig.getFixedParam()).getBytes());

        //获取星节点设备列表
        HashMap<String, Object> stringObjectHashMap = new HashMap<String, Object>();
        //当前时间戳
        stringObjectHashMap.put("timestamp", currentTime);
        //sign
        stringObjectHashMap.put("sign", sign);
        return stringObjectHashMap;
    }


    /**
     * 调用星节点接口
     * @param url   接口url
     * @param starNodeHeader    头信息
     * @param initEquipmentResult   返回信息
     * @return
     */
    public InitEquipmentResult invokeStarNode (String url, HashMap<String, Object> starNodeHeader, InitEquipmentResult initEquipmentResult) {
        /**
         * 发送post
         */
        //获取星节点列表
        HttpClientResult equipmentResult = HttpClientService.sendPost(url, starNodeHeader, null, "http");
        JSONObject equipmentObject = equipmentResult.getObject();
        if (equipmentObject == null) {
            log.error("调用星节点接口失败, 返回信息为空");
            initEquipmentResult.setCode("02");
            initEquipmentResult.setMessage("返回信息为空");
            return initEquipmentResult;
        }

        StarNodeBo starNodeEquipmentBo = JSON.parseObject(equipmentObject.toString(), StarNodeBo.class);
        if (!equipmentResult.isSuccess() || !starNodeEquipmentBo.isSucc()) {
            log.error("调用星节点接口失败");
            initEquipmentResult.setCode("02");
            initEquipmentResult.setMessage("调用星节点接口失败");
            return initEquipmentResult;
        }

        JSONObject starNodeEquipmentData = starNodeEquipmentBo.getData();
        if (starNodeEquipmentData == null) {
            log.error("调用星节点接口失败, 设备信息为空");
            initEquipmentResult.setCode("02");
            initEquipmentResult.setMessage("调用星节点接口失败, 设备信息为空");
            return initEquipmentResult;
        }

        //获取DATA数据
        StarNodeDataBo starNodeDataBo = JSON.parseObject(starNodeEquipmentData.toString(), StarNodeDataBo.class);
        JSONArray starNodeData = starNodeDataBo.getData();
        if (starNodeDataBo.getData().size() == 0) {
            log.error("调用星节点接口失败, 数据DATA为空");
            initEquipmentResult.setCode("02");
            initEquipmentResult.setMessage("数据DATA为空");
            return initEquipmentResult;
        }

//        List<StarNodeEquipmentBo> equipmentDtoList = TypeConvert.jsonToClassList(starNodeData, StarNodeEquipmentBo.class);
        initEquipmentResult.setCode("00");
        initEquipmentResult.setMessage("调用星节点接口成功");
        initEquipmentResult.setObj(starNodeData);
        return initEquipmentResult;
    }

    /**
     * 转化青莲云接口返回数据
     * @param qingLianResult  青莲云接口返回数据
     * @param valueKey  data中key
     * @param initEquipmentResult  返回对象
     * @return
     */
    public InitEquipmentResult qingLianTypeConvert (HttpClientResult qingLianResult, String valueKey, InitEquipmentResult initEquipmentResult) {
        JSONObject object = qingLianResult.getObject();
        QingLianBo qingLianBo = JSON.parseObject(object.toString(), QingLianBo.class);
        if (!qingLianResult.isSuccess() || !qingLianBo.isSucc()) {
            log.error("初始化设备信息失败, 调用青莲云接口失败 msg: {} ", qingLianBo.getMsg());
            initEquipmentResult.setCode("16");
            initEquipmentResult.setMessage("初始化设备信息失败, 调用青莲云接口失败 msg: {}" + qingLianBo.getMsg());
            return initEquipmentResult;
        }

        String qingLianEquipmentData = qingLianBo.getData();
        if (qingLianEquipmentData == null) {
            log.error("初始化设备信息失败, 接口返回数据为空");
            initEquipmentResult.setCode("17");
            initEquipmentResult.setMessage("初始化设备信息失败, 接口返回数据为空");
            return initEquipmentResult;
        }

        JSONObject qingLianEquipmentDataObject = JSON.parseObject(qingLianEquipmentData.toString());
        if (qingLianEquipmentDataObject == null) {
            log.error("初始化设备信息失败, 接口返回数据为空");
            initEquipmentResult.setCode("17");
            initEquipmentResult.setMessage("初始化设备信息失败, 接口返回数据为空");
            return initEquipmentResult;
        }

        JSON singleinfo = (JSON)JSONObject.toJSON(qingLianEquipmentDataObject.get(valueKey));
        if (qingLianEquipmentDataObject == null) {
            log.error("初始化设备信息失败, 接口返回数据为空");
            initEquipmentResult.setCode("17");
            initEquipmentResult.setMessage("初始化设备信息失败, 接口返回数据为空");
            return initEquipmentResult;
        }

        initEquipmentResult.setCode("00");
        initEquipmentResult.setMessage("转化青莲云接口返回数据成功");
        initEquipmentResult.setObj(singleinfo);
        return initEquipmentResult;
    }


    /**
     * 获取青莲云SIGN (32位大写)
     * @param uid  用户ID
     * @param token  用户token
     * @param tt  当前时间
     * @return
     */
    public static String getQingLianSign (String uid, String token, long tt) {
        String MD5Security = SecurityUtils.MD5Encode(uid + token + tt, "UTF-8", "MD5");
        return MD5Security;
    }

    public HttpClientResult executeKey(JSONArray jsonArray) {
        //当前时间
        long currentTime = TimeUtils.getCurrentTime();
        //青莲云sign
        String  qingLianSign= getQingLianSign(xjwxConfig.getUid(), xjwxConfig.getToken(), currentTime);

        //调用设备执行接口
        HashMap<String, Object> operateHashMap = new HashMap<String, Object>();
        operateHashMap.put("uid", xjwxConfig.getUid());
        operateHashMap.put("tt", currentTime);
        operateHashMap.put("sign", qingLianSign);
        operateHashMap.put("data", jsonArray);

        List<NameValuePair> operateBody = HttpClientService.getParams(operateHashMap);

        //批量下发命令控制设备
        HttpClientResult result = HttpClientService.sendPost(qingLianYunUrlConfig.getBatchControlEquipment(), null, operateBody, "https");
        return result;
    }

    /**
     * 调用青莲云获取设备所有数据点的最新一条上报数据
     * @param mac  mac地址
     * @return
     */
    public HttpClientResult lastReportData (String mac) {
        //当前时间
        long currentTime = TimeUtils.getCurrentTime();
        //青莲云sign
        String  qingLianSign= getQingLianSign(xjwxConfig.getUid(), xjwxConfig.getToken(), currentTime);

        //调用设备执行接口
        HashMap<String, Object> operateHashMap = new HashMap<String, Object>();
        operateHashMap.put("mac", mac);
        operateHashMap.put("uid", xjwxConfig.getUid());
        operateHashMap.put("tt", currentTime);
        operateHashMap.put("sign", qingLianSign);

        List<NameValuePair> operateBody = HttpClientService.getParams(operateHashMap);

        HttpClientResult result = HttpClientService.sendPost(qingLianYunUrlConfig.getLastReportData(), null, operateBody, "https");
        return result;
    }

    /**
     * 添加默认分组
     * @param list  设备信息
     * @param initEquipmentResult   初始化返回对象
     * @return
     */
    public InitEquipmentResult addDefaultGroup (List<Equipment> list, InitEquipmentResult initEquipmentResult) {
        //查询默认分组
        List<DivideGroup> divideGroupList = divideGroupService.queryFroVali("", "", "", "", DefaultStatusEnum.OPEN.getCode());
        if (divideGroupList.size() > 1) {
            log.error("初始化设备信息失败, 默认分组存在多个: {} ", divideGroupList);
            initEquipmentResult.setCode("13");
            initEquipmentResult.setMessage("初始化设备信息失败, 默认分组存在多个");
            return initEquipmentResult;
        }
        //默认分组信息
        DivideGroup divideGroupDefault = divideGroupList.get(0);
        //获取币种ID
        Currency currencyEntity = currencyService.getById(divideGroupDefault.getCurrencyId());
        //获取软件包
        List<SoftPackage> softPackageEntity;
        try {
            softPackageEntity = softPackageService.query(currencyEntity.getType(), "", "", DelFlag.NOT_DEL.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化设备信息失败, 获取软件包失败 type: {} ", divideGroupList);
            initEquipmentResult.setCode("14");
            initEquipmentResult.setMessage("初始化设备信息失败, 获取软件包失败");
            return initEquipmentResult;
        }
        if (softPackageEntity == null) {
            log.error("初始化设备信息失败, 软件包数据为空");
            initEquipmentResult.setCode("15");
            initEquipmentResult.setMessage("初始化设备信息失败, 软件包数据为空");
            return initEquipmentResult;
        }

        //软件包
        SoftPackage softPackage = softPackageEntity.get(0);

        //执行脚本(参数之前要空格)
        String value  = "name:" + softPackage.getName() + " version:" + softPackage.getVersion() + " operate:" + operateConfig.getInstallFile();
        JSONArray jsonArray = new JSONArray();
        //拼接参数
        for (int i = 0; i < list.size(); i++) {
            Equipment equipment = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mac", equipment.getMac());
            jsonObject.put("key", operateConfig.getControlMill());
            jsonObject.put("value", value);
            jsonArray.add(i, jsonObject);
        }
        //下载用户
        HttpClientResult httpClientResult = executeKey(jsonArray);
        JSONObject object = httpClientResult.getObject();
        QingLianBo qingLianBo = JSON.parseObject(object.toString(), QingLianBo.class);
        if (!httpClientResult.isSuccess() || !qingLianBo.isSucc()) {
            log.error("初始化设备信息失败, 批量下载安装失败 {}", qingLianBo.getMsg());
            initEquipmentResult.setCode("16");
            initEquipmentResult.setMessage("初始化设备信息失败, 批量下载安装失败 " + qingLianBo.getMsg());
            return initEquipmentResult;
        }

        //软件包
        List<EquipmentSoftPackage> equipmentSoftPackageList = new ArrayList<EquipmentSoftPackage>();
        for (Equipment equipment : list) {
            //设置设备状态
            //设置分组
            equipment.setGroupId(divideGroupDefault.getId());
            //设备运行状态
            equipment.setMiningRunStatus(MiningRunStatus.EXECUTE_MIDDLE.getCode());
            //软件包ID
            equipment.setSoftPackageId(softPackage.getId());

            EquipmentSoftPackage equipmentSoftPackage = new EquipmentSoftPackage();
            //设置设备矿机文件关联
            equipmentSoftPackage.setEquipmentId(equipment.getId());
            //软件包ID
            equipmentSoftPackage.setSoftPackageId(softPackage.getId());
            //矿机ID
            equipmentSoftPackage.setMiningId("");
            equipmentSoftPackageList.add(equipmentSoftPackage);
        }

        //更新分组信息
        boolean updateGroupResult = this.updateBatchById(list);
        if (!updateGroupResult) {
            log.error("初始化设备信息失败, 设备启动成功修改设备分组失败 {}", list);
            initEquipmentResult.setCode("17");
            initEquipmentResult.setMessage("初始化设备信息失败, 设备启动成功修改设备分组失败");
            return initEquipmentResult;
        }

        //添加设备矿机文件夹关联表
        try {
            List<EquipmentSoftPackage> save = equipmentSoftPackageService.save(equipmentSoftPackageList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化设备信息失败, 添加设备矿机文件夹关联表失败 {}", list);
            initEquipmentResult.setCode("18");
            initEquipmentResult.setMessage("初始化设备信息失败, 添加设备矿机文件夹关联表失败");
            return initEquipmentResult;
        }
        log.error("初始化设备信息成功, 数据库新增数据默认分组成功");
        initEquipmentResult.setCode("00");
        initEquipmentResult.setMessage("初始化设备信息失败, 数据库新增数据添加默认分组成功");
        return initEquipmentResult;
    }

    public static void main (String[] args) throws InterruptedException {
        //企业UID(唯一)
        final String XJWX_UID = "10585";
        //企业验证TOKEN(唯一)
        final String XJWX_TOKEN = "d26522e33d694aaddb693c9c99fbd964";

        long l = 1000l;
//            Thread.sleep(l);

        String url = "https://api.qinglianyun.com/open/v3/set/api";
        String urlToday = "https://api.qinglianyun.com/open/v2/get/api";
        //获取获取设备所有数据点的最新一条上报数据
        String newUrl = "https://api.qinglianyun.com/open/v2/getdp/api";
        //戴工mac
        String mac = "14:6b:9c:f6:03:f8";
//        String mac = "e0:b9:a5:21:94:fe";
//        String mac = "68:a3:c4:f4:d5:6d";
//        String mac = "9C:B7:0D:F0:7D:CF";
//        String mac = "d0:df:9a:c0:25:d6";
        long currentTime = TimeUtils.getCurrentTime();
        String  qingLianSign= getQingLianSign(XJWX_UID, XJWX_TOKEN, currentTime);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("mac", mac);
        stringObjectHashMap.put("key", "mill_list");
        stringObjectHashMap.put("value", "矿机列表");
        stringObjectHashMap.put("uid", XJWX_UID);
        stringObjectHashMap.put("tt", currentTime);
        stringObjectHashMap.put("sign", qingLianSign);

        HashMap<String, Object> stringObjectDownloadHashMap = new HashMap<>();
        String value = "name:filestorm.zip version:00.03 operate:install.sh";
        stringObjectDownloadHashMap.put("mac", mac);
        stringObjectDownloadHashMap.put("key", "download");
        stringObjectDownloadHashMap.put("value", value);
        stringObjectDownloadHashMap.put("uid", XJWX_UID);
        stringObjectDownloadHashMap.put("tt", currentTime);
        stringObjectDownloadHashMap.put("sign", qingLianSign);

        HashMap<String, Object> stringObjectHashMap1 = new HashMap<>();
        stringObjectHashMap1.put("mac", mac);
        stringObjectHashMap1.put("key", "mill_list");
        stringObjectHashMap1.put("uid", XJWX_UID);
        stringObjectHashMap1.put("tt", currentTime);
        stringObjectHashMap1.put("sign", qingLianSign);

        HashMap<String, Object> newStringObjectHashMap2 = new HashMap<String, Object>();
        newStringObjectHashMap2.put("mac", mac);
        newStringObjectHashMap2.put("uid", XJWX_UID);
        newStringObjectHashMap2.put("tt", currentTime);
        newStringObjectHashMap2.put("sign", qingLianSign);

//        //获取参数对象
        List<NameValuePair> params = HttpClientService.getParams(stringObjectHashMap);
        //下载文件夹
        List<NameValuePair> stringObjectDownload = HttpClientService.getParams(stringObjectDownloadHashMap);
        List<NameValuePair> paramToday = HttpClientService.getParams(stringObjectHashMap1);
        //获取最新的上传文件
        List<NameValuePair> newParam = HttpClientService.getParams(newStringObjectHashMap2);

        //获取设备列表
//        HttpClientResult equipmentList = HttpClientService.sendPost(url, null, params, "https");
        //下载矿机文件夹
            HttpClientResult equipmentList = HttpClientService.sendPost(url, null, stringObjectDownload, "https");

        //获取设备今天上传数据
//        HttpClientResult today = HttpClientService.sendPost(urlToday, null, paramToday, "https");
//        System.out.println("onlineTimeResult----------" + today);

//        HttpClientResult newResult = HttpClientService.sendPost(newUrl, null, newParam, "https");
//        System.out.println("newResult----------" + newResult);

//        test();
    }

    public static void test () {
        Currency currency = new Currency() {
            @Override
            public boolean equals(Object obj) {
                if(obj == this) {return true;}
                if (!(obj instanceof Currency)) { return false;}
                Currency re = (Currency) obj;
                return getType().equals(re.getType());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getType());
            }
        };
        ArrayList<Currency> currencies = new ArrayList<Currency>();
        ArrayList<Currency> arrayListTest = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0;i < 10; i++) {
            Currency currency1 = new Currency() {
                @Override
                public boolean equals(Object obj) {
                    if(obj == this) {return true;}
                    if (!(obj instanceof Currency)) { return false;}
                    Currency re = (Currency) obj;
                    return getType().equals(re.getType());
                }

                @Override
                public int hashCode() {
                    return Objects.hash(getType());
                }
            };
            currency1.setId(i);
            currency1.setType(String.valueOf(i));
            currencies.add(currency1);
        }
        arrayListTest = currencies;

        ArrayList<Currency> currencies1 = new ArrayList<Currency>();
        Currency currency1 = new Currency() {
            @Override
            public boolean equals(Object obj) {
                if(obj == this) {return true;}
                if (!(obj instanceof Currency)) { return false;}
                Currency re = (Currency) obj;
                return getType().equals(re.getType());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getType());
            }
        };
//        currency1.setId(1);
        currency1.setType("1");
        currencies1.add(currency1);

        HashMap<Currency, Integer> currencyIntegerHashMap = new HashMap<>(currencies.size());
        ArrayList<Currency> currencies2 = new ArrayList<>();
        for (Currency curren : currencies) {
            currencyIntegerHashMap.put(curren, 1);
        }
        for (Currency currency4 : currencies1) {
            if (currencyIntegerHashMap.get(currency4) == null) {
                currencies2.add(currency4);
            }
            System.out.println(currency4.getId());
            System.out.println(currency4.getType());
        }

        boolean contains = currencies.contains(currencies1);
        currencies.retainAll(currencies1);
        arrayListTest.removeAll(currencies);
        System.out.println("contains------" + contains);
    }

}
