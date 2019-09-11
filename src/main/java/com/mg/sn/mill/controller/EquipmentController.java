package com.mg.sn.mill.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.ConfigProperties.OperateConfig;
import com.mg.sn.mill.model.entity.*;
import com.mg.sn.mill.service.IDivideGroupService;
import com.mg.sn.mill.service.IEquipmentService;
import com.mg.sn.mill.service.IEquipmentSoftPackageService;
import com.mg.sn.mill.service.ISoftPackageService;
import com.mg.sn.utils.Enum.MiningRunStatus;
import com.mg.sn.utils.annotation.Auth;
import com.mg.sn.utils.baseController.StarNodeBaseController;
import com.mg.sn.utils.paramObject.IDRequestObject;
import com.mg.sn.utils.result.CommonConstant;
import com.mg.sn.utils.result.InitEquipmentResult;
import com.mg.sn.utils.result.StarNodeResultObject;
import com.mg.sn.utils.result.StarNodeWrappedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * <p>
 *  设备模块
 * </p>
 *
 * @author hcy
 * @since 2019-08-14
 */
@Api(description = "设备模块")
@Auth
@RestController
@RequestMapping("/equipmentController")
public class EquipmentController extends StarNodeBaseController {

	@Autowired
	private OperateConfig operateConfig;

	@Autowired
	private IEquipmentService equipmentService;

	@Autowired
	private IDivideGroupService divideGroupService;

	@Autowired
	private ISoftPackageService softPackageService;

	@Autowired
    private IEquipmentSoftPackageService equipmentSoftPackageService;

	private static final Logger log = LoggerFactory.getLogger(EquipmentController.class);


	@ApiOperation(value="初始化设备信息", notes="初始化设备信息")
	@PostMapping(value = "initEquipment" ,produces = "application/json;charset=UTF-8")
	public StarNodeWrappedResult initEquipment () {
		//获取设备信息
		InitEquipmentResult equipment = equipmentService.initEquipment();
		if (!equipment.isSucc()) {
			log.error("初始化设备信息失败");
			return StarNodeWrappedResult.failWrapedResult("初始化设备信息失败", equipment);
		}
		return StarNodeWrappedResult.successWrapedResult("初始化设备信息成功", equipment);
	}

	@ApiOperation(value="查询设备信息", notes="查询设备信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "名称", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "ip", value = "ip地址", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "userName", value = "用户名", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "type", value = "设备类型(1:正常;2:失联;4:未分组)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "pageIndex", value = "页码", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "address", value = "地址", required = false, dataType = "String", paramType = "query")
	})
	@PostMapping(value = "query" ,produces = "application/json;charset=UTF-8")
	public StarNodeWrappedResult query (String name, String ip, String type, String userName,
										@RequestParam(value = "pageIndex", defaultValue = "1")String  pageIndex,
										@RequestParam(value = "pageSize", defaultValue = "10")String  pageSize) {

		StarNodeResultObject starNodeResultObject = equipmentService.queryPage(name, ip, type, userName, pageIndex, pageSize);

		//统计设备数据
		StarNodeResultObject equipmentStatistics;
		try {
			equipmentStatistics = equipmentService.statisticsEquipment();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("统计设备数据失败");
			return StarNodeWrappedResult.failWrapedResult("统计设备数据失败");
		}

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("equipmentList", starNodeResultObject);
		resultMap.put("equipmentStatistics", equipmentStatistics);

		if (starNodeResultObject == null) {
			log.error("查询设备信息失败");
			return StarNodeWrappedResult.failWrapedResult("查询设备信息失败");
		}
		return StarNodeWrappedResult.successWrapedResult("查询设备信息失败", resultMap);
	}

	@ApiOperation(value="设置设备分组", notes="设置设备分组")
	@PostMapping(value = "setDivideGroup" ,produces = "application/json;charset=UTF-8")
	public StarNodeWrappedResult setDivideGroup (@RequestBody IDRequestObject idRequestObject) {
		//设备ID
		String[] ids = idRequestObject.getIds();
		//分组ID
		String groupId = idRequestObject.getGroupId();

		if (ids.length == 0) {
			log.error("设置设备分组失败， 设备ID为空");
			return StarNodeWrappedResult.failWrapedResult("设置设备分组失败， 设备ID为空");
		}
		
		if (StringUtils.isEmpty(groupId)) {
			log.error("设置设备分组失败， 分组信息为空");
			return StarNodeWrappedResult.failWrapedResult("设置设备分组失败， 分组信息为空");
		}

		//获取设备详细信息
		Collection<Equipment> equipmentConll = equipmentService.listByIds(Arrays.asList(ids));
		if (equipmentConll.size() == 0) {
            log.error("设置设备分组失败， 设备信息为空");
            return StarNodeWrappedResult.failWrapedResult("设置设备分组失败， 设备信息为空");
        }

        List<Equipment> equipmentList = new ArrayList<Equipment>(equipmentConll);
        //暂停设备集合
        List<Equipment> stopEquipmentList = new ArrayList<Equipment>();
        //下载设备集合
        ArrayList<Equipment> downloadEquipment = new ArrayList<Equipment>();
        //矿机运行状态
        for (Equipment equipment : equipmentList) {
            //是否在线
            String isOnline = equipment.getIsOnline();
            //设备矿机运行状态
            String miningRunStatus = equipment.getMiningRunStatus();
            //设备不在线
            if (com.mg.sn.utils.common.StringUtils.stringEquals("false", isOnline)) {
                log.error("设置设备分组失败， 设备失联");
                return StarNodeWrappedResult.failWrapedResult("设置设备分组失败， 设备失联");
            }

            //矿机运行状态为(执行中或开启)
            if (com.mg.sn.utils.common.StringUtils.stringEquals(MiningRunStatus.EXECUTE_MIDDLE.getCode(), miningRunStatus) ||
                    com.mg.sn.utils.common.StringUtils.stringEquals(MiningRunStatus.START.getCode(), miningRunStatus)) {
                log.error("设置设备分组失败， 设备矿机正在运行");
                return StarNodeWrappedResult.failWrapedResult("设置设备分组失败， 设备矿机正在运行");
            }
        }


        InitEquipmentResult initEquipmentResult = null;
		try {
		    initEquipmentResult = equipmentService.setDivideGroup(equipmentList, groupId);
        } catch (Exception e) {
			e.printStackTrace();
			log.error("设置设备: {} 分组失败", initEquipmentResult);
			return StarNodeWrappedResult.failWrapedResult("设置设备分组失败", initEquipmentResult);
		}

		if (!initEquipmentResult.isSucc()) {
            return StarNodeWrappedResult.failWrapedResult("设置设备分组失败", initEquipmentResult);
        }

		return StarNodeWrappedResult.successWrapedResult("设置设备分组成功", initEquipmentResult);
	}

	@ApiOperation(value="更新软件包", notes="更新软件包")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "名称", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "version", value = "版本", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "format", value = "格式", required = false, dataType = "String", paramType = "query")
	})
	@PostMapping(value = "updateSoftPackage" ,produces = "application/json;charset=UTF-8")
	public StarNodeWrappedResult updateSoftPackage (String name, String version, String format) {
		//获取用户ID
		String userId = this.getUserId();

		if (StringUtils.isEmpty(name)) {
			log.error("更新软件包失败，名称为空");
			return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 名称为空");
		}

		if (StringUtils.isEmpty(version)) {
			log.error("更新软件包失败，版本为空");
			return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 版本为空");
		}

		if (StringUtils.isEmpty(format)) {
			log.error("更新软件包失败， 格式为空");
			return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 格式为空");
		}

		if (!com.mg.sn.utils.common.StringUtils.stringEquals(format, "zip")) {
			log.error("更新软件包失败， 格式必须为zip");
			return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 格式必须为zip");
		}

		//验证名称和版本信息是否存在
		List<SoftPackage> query;
		try {
			query = softPackageService.query(name, version, "", "");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("更新软件包失败， 判断名称和版本信息是否存在失败");
			return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 判断名称和版本信息是否存在失败");
		}
		if (query.size() > 0) {
			log.error("更新软件包失败， 名称和版本信息已经存在");
			return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 名称和版本信息已经存在");
		}

		List<SoftPackage> nameVali;
		try {
			nameVali = softPackageService.query(name, "", "", "");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("更新软件包失败， 判断名称和版本信息是否存在失败");
			return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 判断名称和版本信息是否存在失败");
		}

		if (nameVali.size() > 0) {
			SoftPackage updateAndSaveResult;
			try {
				updateAndSaveResult = softPackageService.updateAndSave(nameVali, name, version, format, userId);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("更新软件包失败， 更新旧版本新增新版本失败");
				return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 更新旧版本新增新版本失败");
			}
			//更新软件包成功
			return StarNodeWrappedResult.successWrapedResult("更新软件包成功", updateAndSaveResult);
		} else if (nameVali.size() == 0) {
			//保存软件包信息
			SoftPackage saveResult;
			try {
				saveResult = softPackageService.save(name, version, format, userId);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("更新软件包失败， 保存软件包信息失败");
				return StarNodeWrappedResult.failWrapedResult("更新软件包失败， 保存软件包信息失败");
			}
			//更新软件包成功
			return StarNodeWrappedResult.successWrapedResult("更新软件包成功", saveResult);
		}

		return StarNodeWrappedResult.successWrapedResult("更新软件包成功", null);
	}

    @ApiOperation(value="控制矿机", notes="控制矿机")
	@NotNull
    @PostMapping(value = "controlMining" ,produces = "application/json;charset=UTF-8")
	public StarNodeWrappedResult controlMining (@RequestBody IDRequestObject idRequestObject) {

	    //操作指令
        String operate = "";

	    //设备ID
        String[] ids = idRequestObject.getIds();

        //控制指令
        String controlKey = idRequestObject.getControlKey();

        if (ids.length == 0) {
            log.error("控制矿机失败， 设备ID为空");
            return StarNodeWrappedResult.failWrapedResult("控制矿机失败， 设备ID为空");
        }

        if (StringUtils.isEmpty(controlKey)) {
            log.error("控制矿机失败， 控制指令为空");
            return StarNodeWrappedResult.failWrapedResult("控制矿机失败， 控制指令为空");
        }

        //获取设备详细信息
        Collection<Equipment> equipmentConll = equipmentService.listByIds(Arrays.asList(ids));
        if (equipmentConll.size() == 0) {
            log.error("控制矿机失败， 设备信息为空");
            return StarNodeWrappedResult.failWrapedResult("控制矿机失败， 设备信息为空");
        }

        List<Equipment> equipmentList = new ArrayList<Equipment>(equipmentConll);
        //批量value
        JSONArray jsonArray = new JSONArray();
        for (Equipment equipment : equipmentList) {
            //是否在线
            String isOnline = equipment.getIsOnline();
            //设备矿机运行状态
            String miningRunStatus = equipment.getMiningRunStatus();
            //设备不在线
            if (com.mg.sn.utils.common.StringUtils.stringEquals("false", isOnline)) {
                log.error("控制矿机失败， 设备失联");
                return StarNodeWrappedResult.failWrapedResult("控制矿机失败， 设备失联");
            }

            SoftPackage softPackage = softPackageService.getById(equipment.getSoftPackageId());
            //拼接value
            JSONObject jsonObject = new JSONObject();
            switch (controlKey) {
                //启动进程
                case  "1":
                    if (com.mg.sn.utils.common.StringUtils.stringEquals(equipment.getMiningRunStatus(), MiningRunStatus.EXECUTE_MIDDLE.getCode())) {
                        log.error("开启进程失败， 进程正在执行中 ");
                        return StarNodeWrappedResult.failWrapedResult("开启进程失败， 进程正在执行中");
                    }
                    if (com.mg.sn.utils.common.StringUtils.stringEquals(equipment.getMiningRunStatus(), MiningRunStatus.START.getCode())) {
                        log.error("开启进程失败， 进程已经启动 ");
                        return StarNodeWrappedResult.failWrapedResult("开启进程失败， 进程已经启动");
                    }
                    operate = operateConfig.getStartProcess();
                    break;
                //暂停进程
                case "2" :
                    if (com.mg.sn.utils.common.StringUtils.stringEquals(equipment.getMiningRunStatus(), MiningRunStatus.EXECUTE_MIDDLE.getCode())) {
                        log.error("开启进程失败， 进程正在执行中 ");
                        return StarNodeWrappedResult.failWrapedResult("开启进程失败， 进程正在执行中");
                    }
                    if (com.mg.sn.utils.common.StringUtils.stringEquals(equipment.getMiningRunStatus(), MiningRunStatus.STOP.getCode())) {
                        log.error("开启进程失败， 进程已经暂停");
                        return StarNodeWrappedResult.failWrapedResult("开启进程失败， 进程已经暂停");
                    }
                    operate = operateConfig.getStopProcess();
                    break;
                //重启进程
                case "3" :
					operate = operateConfig.getRestartProcess();
                    break;
                //重启矿机
                case "4" :
					operate = operateConfig.getRestartServer();
                    break;
            }
            String value = "name:" + softPackage.getName() + " version:" + softPackage.getVersion() + " operate:" + operate;
            jsonObject.put("mac", equipment.getMac());
            jsonObject.put("key", operateConfig.getControlMill());
            jsonObject.put("value", value);
            jsonArray.add(jsonObject);

            //更改执行状态
            equipment.setMiningRunStatus(MiningRunStatus.EXECUTE_MIDDLE.getCode());
        }

        boolean result;
        try {
            result= equipmentService.controlMining(jsonArray, equipmentList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("控制矿机失败");
            return StarNodeWrappedResult.failWrapedResult("控制矿机失败");
        }

        //成功
        return StarNodeWrappedResult.successWrapedResult("控制矿机成功", result);
    }

    @ApiOperation(value="总览", notes="总览")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "divideGroupName", value = "矿机分组名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageIndex", value = "页码", required = false, dataType = "", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "statistics" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult statistics (String divideGroupName,
                                             @RequestParam(value = "pageIndex", defaultValue = "1")String  pageIndex,
                                             @RequestParam(value = "pageSize", defaultValue = "10")String  pageSize) {

		//统计分组数据
        StarNodeResultObject divideGroupStatistics;
        try {
            divideGroupStatistics = equipmentService.statisticsDivideGroup(divideGroupName, pageIndex, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("统计分组数据失败");
            return StarNodeWrappedResult.failWrapedResult("统计分组数据失败");
        }

        //统计设备数据
        StarNodeResultObject equipmentStatistics;
        try {
            equipmentStatistics = equipmentService.statisticsEquipment();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("统计设备数据失败");
            return StarNodeWrappedResult.failWrapedResult("统计设备数据失败");
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("divideGroupStatistics", divideGroupStatistics);
        map.put("equipmentStatistics", equipmentStatistics);

        //统计成功
        return StarNodeWrappedResult.successWrapedResult("统计总览成功", map);
    }

}