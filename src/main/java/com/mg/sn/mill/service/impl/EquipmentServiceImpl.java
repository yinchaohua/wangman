package com.mg.sn.mill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.sn.mill.mapper.DivideGroupMapper;
import com.mg.sn.mill.mapper.EquipmentMapper;
import com.mg.sn.mill.model.bo.*;
import com.mg.sn.mill.model.dto.EquipmentDto;
import com.mg.sn.mill.model.dto.QingLianEquipmentDetailDto;
import com.mg.sn.mill.model.dto.WalletDto;
import com.mg.sn.mill.model.entity.Currency;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.model.entity.Equipment;
import com.mg.sn.mill.service.IEquipmentService;
import com.mg.sn.utils.Enum.RunStatus;
import com.mg.sn.utils.common.StringUtils;
import com.mg.sn.utils.common.TimeUtils;
import com.mg.sn.utils.common.TypeConvert;
import com.mg.sn.utils.httpclient.HttpClientResult;
import com.mg.sn.utils.httpclient.HttpClientService;
import com.mg.sn.utils.redis.RedisUtil;
import com.mg.sn.utils.result.CommonConstant;
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
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.rmi.ServerException;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDate;
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

	//星节点接口调用特殊字符
	private static final String STAR_NODE = "STAR_NODE";
	//星节点设备调用URL
    private static final String STAR_NODE_EQUIPMENT_URL = "http://xjd.caiba.pro/api/api/synch/device/list";
	//星节点用户调用URL
    private static final String STAR_NODE_USER_URL = "http://xjd.caiba.pro/api/api/synch/user/list";
    //星节点调用公钥
    private static final String STAR_NODE_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXErdRy/ZamvJBMubIN5P5hTOi56pT6wVIdEHcAf7qbaz6l1TmZq/URjYhlDocbiIOO9FW/yQTf/iiBjwtLjoPgickRMHEcOKouiaVAaDwS4bDexbGRHZzTll8ByXsJ4+G6Fvq+PeoksJEqJNgtR4xIxAeeHXOvof/XUmCdvrFBwIDAQAB";

    //企业UID(唯一)
    private static final String XJWX_UID = "10585";
    //企业验证TOKEN(唯一)
    private static final String XJWX_TOKEN = "d26522e33d694aaddb693c9c99fbd964";
    //青莲云获取单个设备URL
    private static final String QINGLIAN_DEVICE_SINGLEINFO = "https://api.qinglianyun.com/open/v2/device/singleinfo";

    //获取设备在线时间
    private static final String QINGLIAN_DEVICE_EQUIPMENT_ONLINE_TIME = "https://api.qinglianyun.com/open/v2/get/device/online/time";


	@Autowired
	private EquipmentMapper equipmentMapper;

    @Resource
    private DivideGroupMapper divideGroupMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public InitEquipmentResult initEquipment(List<DivideGroup> divideGroup) {

        boolean defaultExsit = false;
        //如果存在设置true
        if (divideGroup.size() == 1) {
            defaultExsit = true;
        }

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
        InitEquipmentResult starNodeEquipmentResult = invokeStarNodeEquipmentList(starNodeHeader, initEquipmentResult);
        //获取设备失败
        if (!starNodeEquipmentResult.isSucc()) {
            return starNodeEquipmentResult;
        }
        //获取设备成功
        List<StarNodeEquipmentBo> starNodeEquipmentBoList = (List<StarNodeEquipmentBo>) starNodeEquipmentResult.getObj();

        //获取星节点用户信息
        InitEquipmentResult StarNodeUserResult = invokeStarNodeUserList(starNodeHeader, initEquipmentResult);
        //获取设备失败
        if (!StarNodeUserResult.isSucc()) {
            return StarNodeUserResult;
        }
        //获取用户成功
        List<StarNodeUserBo> starNodeUserBoList = (List<StarNodeUserBo>) StarNodeUserResult.getObj();

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

        //调用青莲云接口获取设备详细信息
        ArrayList<Equipment> removeList = new ArrayList<>();
        for (Equipment starNodeEquipment : starNodeEquipmentList) {
            //获取当前时间戳
            long currentTime = TimeUtils.getCurrentTime();
            //生成32位大写MD5秘钥(uid + token + tt)
            String sign = getQingLianSign(XJWX_UID, XJWX_TOKEN, currentTime);
            Integer qingLianUserId = starNodeEquipment.getQinglianUserId();
            String qingLianUserToken = starNodeEquipment.getQinglianUserToken();
            String mac = starNodeEquipment.getMac();

            HashMap<String, Object> qingLianMap = new HashMap<>();
            //用户ID
            qingLianMap.put("uid", XJWX_UID);
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
            HttpClientResult qingLianResult = HttpClientService.sendPost(QINGLIAN_DEVICE_SINGLEINFO, null, qingLianBody, "https");
            if (qingLianResult == null) {
                log.error("获取设备信息失败, 调用青莲云单个设备返回结果为空 参数 {} ", qingLianBody);
                removeList.add(starNodeEquipment);
                continue;
            }

            InitEquipmentResult convertResult = qingLianTypeConvert(qingLianResult, "singleinfo", initEquipmentResult);
            if (!convertResult.isSucc()) {
                log.error("获取设备信息失败, 调用青莲云单个设备失败 参数 {} ", qingLianBody);
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
            onlineTimeMap.put("uid", XJWX_UID);
            onlineTimeMap.put("tt", currentTime);
            onlineTimeMap.put("sign", sign);

            //获取参数对象
            List<NameValuePair> onlineTimeBody = HttpClientService.getParams(onlineTimeMap);

            //获取设备在线时间
            HttpClientResult onlineTimeResult = HttpClientService.sendPost(QINGLIAN_DEVICE_EQUIPMENT_ONLINE_TIME, null, onlineTimeBody, "https");
            if (onlineTimeResult == null) {
                log.error("获取设备信息失败, 获取设备在线时长信息为空, 参数 {} ", onlineTimeBody);
                continue;
            }

            InitEquipmentResult onlineConvertResult = qingLianTypeConvert(onlineTimeResult, "data", initEquipmentResult);
            if (!onlineConvertResult.isSucc()) {
//                return onlineConvertResult;
                continue;
            }
            //转换成功
            JSON onlineTimeJson = (JSON) onlineConvertResult.getObj();
            List<QingLianOnlineTimeBo> qingLianOnlineTime = JSONObject.parseArray(onlineTimeJson.toString(), QingLianOnlineTimeBo.class);

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
            //设备默认分组
            if (defaultExsit) {
                starNodeEquipment.setGroupId(divideGroup.get(0).getId());
            }
        }

        //移除报错数据
        starNodeEquipmentList.removeAll(removeList);

        //新增的设备信息
        ArrayList<Equipment> addEquipment = new ArrayList<Equipment>();
        ArrayList<Equipment> updateEquipment = new ArrayList<Equipment>();
        List<Equipment> oldDataList = this.list();
        redisUtil.set("EQUIPMENT_LIST_KEY", oldDataList, 3000);
        List<Equipment> equipment_list_key = (List<Equipment>)redisUtil.get("EQUIPMENT_LIST_KEY");

        //初始化(表中不存在数据)
        if (equipment_list_key.size() == 0) {
            boolean result = this.saveBatch(starNodeEquipmentList);
            if (!result) {
                log.error("初始化设备信息失败, 保存设备信息失败 {}", starNodeEquipmentList);
                initEquipmentResult.setCode("13");
                initEquipmentResult.setMessage("初始化设备信息失败, 保存设备信息失败");
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
//                    oldEquipment.setYtdOnlineHour(newEquipment.getYtdOnlineHour());
//                    //昨日在线分钟
//                    oldEquipment.setYtdOnlineMinute(newEquipment.getTotalOnlineMinute());
//                    //累计在线小时
//                    oldEquipment.setTotalOnlineHour(newEquipment.getTotalOnlineHour());
//                    //累计在线分钟
//                    oldEquipment.setTotalOnlineMinute(newEquipment.getYtdOnlineMinute());
                    updateEquipment.add(oldEquipment);
                }
            }

            //更新已经存在设备数据
            if (updateEquipment.size() != 0) {
                boolean updateResult = this.updateBatchById(updateEquipment);
                if (!updateResult) {
                    log.error("初始化设备失败, 已存在数据: {} 更新失败", updateEquipment);
                    initEquipmentResult.setCode("14");
                    initEquipmentResult.setMessage("初始化设备信息失败, 已存在数据更新失败");
                    return initEquipmentResult;
                }
            }

            //添加新设备数据
            if (addEquipment.size() != 0) {
                boolean addResult = this.saveBatch(addEquipment);
                if (!addResult) {
                    log.error("初始化设备失败, 新添加设备数据: {} 插入失败", addEquipment);
                    initEquipmentResult.setCode("15");
                    initEquipmentResult.setMessage("初始化设备信息失败, 新添加设备数据插入失败");
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
    public StarNodeResultObject queryPage(String name, String ip, String pageIndex, String pageSize) {
        try {
            Page page = new Page(Long.parseLong(pageIndex), Long.parseLong(pageSize));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("ip", ip);
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
    public Collection<Equipment> setDivideGroup(String[] ids, String groupId) throws Exception {
        Collection<Equipment> equipment = this.listByIds(Arrays.asList(ids));

        for (Equipment Equipment : equipment) {
            Equipment.setGroupId(Integer.parseInt(groupId));
        }
//        //需要添加分组设备
//        ArrayList<Equipment>  addGroupList = new ArrayList<Equipment>();
//        //需要更新分组设备
//        ArrayList<Equipment>  updateGroupList = new ArrayList<Equipment>();
//        for (Equipment Equipment : equipment) {
//            //设备不存在分组添加分组
//            if (Equipment.getGroupId() == null) {
//                Equipment.setGroupId(Integer.parseInt(groupId));
//                addGroupList.add(Equipment);
//            } else {  //不为空， 停止矿机再分组
//                updateGroupList.add(Equipment);
//            }
//        }

        //添加分组设备
        boolean addGroupResult = this.updateBatchById(equipment);
        if (!addGroupResult) {
            throw new ServerException("更新分组设备信息失败");
        }
        return equipment;
    }

    /**
     * 获取星节点头信息
     * @return
     */
    public HashMap<String, Object> getStarNodeHeader () throws Exception {

        RSAPublicKey rsaPublicKey = null;
        //星节点传入sign
        String sign = "";
        //获取当前时间戳
        long currentTime = TimeUtils.getCurrentTime();

        rsaPublicKey = loadPublicKey(STAR_NODE_PUBLIC_KEY);
        sign = encrypt(rsaPublicKey, (currentTime + STAR_NODE).getBytes());

        //获取星节点设备列表
        HashMap<String, Object> stringObjectHashMap = new HashMap<String, Object>();
        //当前时间戳
        stringObjectHashMap.put("timestamp", currentTime);
        //sign
        stringObjectHashMap.put("sign", sign);
        return stringObjectHashMap;
    }


    /**
     * 获取星节点设备信息
     * @param starNodeHeader  请求头
     * @param initEquipmentResult  返回对象
     * @return
     */
    public InitEquipmentResult invokeStarNodeEquipmentList (HashMap<String, Object> starNodeHeader, InitEquipmentResult initEquipmentResult) {
        /**
         * 发送post
         */
        //获取星节点设备列表
        HttpClientResult equipmentResult = HttpClientService.sendPost(STAR_NODE_EQUIPMENT_URL, starNodeHeader, null, "http");
        JSONObject equipmentObject = equipmentResult.getObject();
        if (equipmentObject == null) {
            log.error("获取设备信息失败, 获取星节点设备信息失败");
            initEquipmentResult.setCode("02");
            initEquipmentResult.setMessage("获取设备信息失败, 获取星节点设备信息失败");
            return initEquipmentResult;
        }

        StarNodeBo starNodeEquipmentBo = JSON.parseObject(equipmentObject.toString(), StarNodeBo.class);
        if (!equipmentResult.isSuccess() || !starNodeEquipmentBo.isSucc()) {
            log.error("获取设备信息失败, 调用星节点设备信息失败");
            initEquipmentResult.setCode("03");
            initEquipmentResult.setMessage("获取设备信息失败, 调用星节点设备接口失败");
            return initEquipmentResult;
        }

        JSONObject starNodeEquipmentData = starNodeEquipmentBo.getData();
        if (starNodeEquipmentData == null) {
            log.error("获取设备信息失败, 设备信息为空");
            initEquipmentResult.setCode("04");
            initEquipmentResult.setMessage("获取设备信息失败, 设备信息为空");
            return initEquipmentResult;
        }

        //获取DATA数据
        StarNodeDataBo starNodeDataBo = JSON.parseObject(starNodeEquipmentData.toString(), StarNodeDataBo.class);
        if (starNodeDataBo.getCount() == 0) {
            log.error("获取设备信息失败, 设备返回数据DATA为空");
            initEquipmentResult.setCode("04");
            initEquipmentResult.setMessage("获取设备信息失败, 设备信息为空");
            return initEquipmentResult;
        }
        JSONArray starNodeData = starNodeDataBo.getData();
        if (starNodeDataBo.getCount() == 0) {
            log.error("获取设备信息失败, 设备返回数据DATA为空");
            initEquipmentResult.setCode("05");
            initEquipmentResult.setMessage("获取设备信息失败, 设备信息为空");
            return initEquipmentResult;
        }

        List<StarNodeEquipmentBo> equipmentDtoList = TypeConvert.jsonToClassList(starNodeData, StarNodeEquipmentBo.class);
        initEquipmentResult.setCode("00");
        initEquipmentResult.setMessage("获取设备信息成功");
        initEquipmentResult.setObj(equipmentDtoList);
        return initEquipmentResult;
    }

    /**
     * 获取星节点用户信息
     * @param starNodeHeader  请求头
     * @param initEquipmentResult  返回对象
     * @return
     */
    public InitEquipmentResult invokeStarNodeUserList (HashMap<String, Object> starNodeHeader, InitEquipmentResult initEquipmentResult) {
        //获取星节点用户信息
        HttpClientResult userResult = HttpClientService.sendPost(STAR_NODE_USER_URL, starNodeHeader, null, "http");
        JSONObject userObject = userResult.getObject();
        if (userObject == null) {
            log.error("获取星节点用户信息失败, 星节点用户信息为空, 请求信息 {} ", starNodeHeader);
            initEquipmentResult.setCode("06");
            initEquipmentResult.setMessage("获取星节点用户信息失败, 星节点用户信息为空");
            return initEquipmentResult;
        }

        StarNodeBo starNodeUserBo = JSON.parseObject(userObject.toString(), StarNodeBo.class);
        if (!userResult.isSuccess() || !starNodeUserBo.isSucc()) {
            log.error("获取星节点用户信息失败, 调用星节点用户接口失败, 请求信息 {} ", starNodeHeader);
            initEquipmentResult.setCode("07");
            initEquipmentResult.setMessage("获取星节点用户信息失败, 调用星节点用户接口失败");
            return null;
        }

        //获取返回数据
        JSONObject starNodeUserData = starNodeUserBo.getData();
        if (starNodeUserData == null) {
            log.error("获取设备信息失败, 星节点用户信息DATA为空");
            initEquipmentResult.setCode("08");
            initEquipmentResult.setMessage("获取星节点用户信息失败, 星节点用户信息为空");
            return initEquipmentResult;
        }

        //获取DATA数据
        StarNodeDataBo starNodeDataBo = JSON.parseObject(starNodeUserData.toString(), StarNodeDataBo.class);
        if (starNodeDataBo.getCount() == 0) {
            log.error("获取星节点用户信息失败, 星节点用户信息为空");
            initEquipmentResult.setCode("09");
            initEquipmentResult.setMessage("获取星节点用户信息失败, 星节点用户信息为空");
            return initEquipmentResult;
        }

        JSONArray starNodeData = starNodeDataBo.getData();
        if (starNodeData == null) {
            log.error("获取星节点用户信息失败, 星节点用户信息为空");
            initEquipmentResult.setCode("10");
            initEquipmentResult.setMessage("获取星节点用户信息失败, 星节点用户信息为空");
            return initEquipmentResult;
        }

        //转换成对象
        List<StarNodeUserBo> starNodeUserBoList = TypeConvert.jsonToClassList(starNodeData, StarNodeUserBo.class);
        log.error("获取星节点用户信息成功");
        initEquipmentResult.setCode("00");
        initEquipmentResult.setMessage("获取星节点用户信息成功");
        initEquipmentResult.setObj(starNodeUserBoList);
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
            log.error("初始化设备信息失败, 调用青莲云接口失败");
            initEquipmentResult.setCode("16");
            initEquipmentResult.setMessage("初始化设备信息失败, 调用青莲云接口失败");
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

    public static void main (String[] args) throws InterruptedException {
        long l = 1000l;
//            Thread.sleep(l);

        String url = "https://api.qinglianyun.com/open/v3/set/api";
        String urlToday = "https://api.qinglianyun.com/open/v2/get/api";
//        String mac = "14:6b:9c:f6:03:f8";
//        String mac = "e0:b9:a5:21:94:fe";
        String mac = "68:a3:c4:f4:d5:6d";
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
        String value = "name:filestorm.zip version:00.02 operate:install.sh";
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

//        //获取参数对象
        List<NameValuePair> params = HttpClientService.getParams(stringObjectHashMap);
        //下载文件夹
        List<NameValuePair> stringObjectDownload = HttpClientService.getParams(stringObjectDownloadHashMap);
        List<NameValuePair> paramToday = HttpClientService.getParams(stringObjectHashMap1);

        //获取设备列表
//        HttpClientResult equipmentList = HttpClientService.sendPost(url, null, params, "https");
            HttpClientResult equipmentList = HttpClientService.sendPost(url, null, stringObjectDownload, "https");

        //获取设备今天上传数据
//        HttpClientResult today = HttpClientService.sendPost(urlToday, null, paramToday, "https");
//        System.out.println("onlineTimeResult----------" + today);


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
