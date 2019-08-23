package com.mg.sn.mill.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.model.entity.Equipment;
import com.mg.sn.mill.service.IDivideGroupService;
import com.mg.sn.mill.service.IEquipmentService;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hcy
 * @since 2019-08-14
 */
@Api(description = "设备模块")
@RestController
@RequestMapping("/equipmentController")
public class EquipmentController {

	@Autowired
	private IEquipmentService equipmentService;

	@Autowired
	private IDivideGroupService divideGroupService;

	private static final Logger log = LoggerFactory.getLogger(EquipmentController.class);

	@ApiOperation(value="初始化设备信息", notes="初始化设备信息")
	@PostMapping(value = "initEquipment" ,produces = "application/json;charset=UTF-8")
	public StarNodeWrappedResult initEquipment () {

		//返回对象
		InitEquipmentResult initEquipmentResult = new InitEquipmentResult();

		//新增设备添加默认分组
		List<DivideGroup> divideGroup;
		try {
			divideGroup = divideGroupService.queryFroVali("", "", "", "", CommonConstant.DEFAULT_STATUS_START);
			if (divideGroup.size() > 1) {
				log.error("初始化设备失败, 默认分组不能存在多个");
				return StarNodeWrappedResult.failWrapedResult("初始化设备失败, 默认分组不能存在多个");
			}
		} catch (Exception e) {
			log.error("查询默认分组信息失败", e);
			return StarNodeWrappedResult.failWrapedResult("初始化设备失败, 查询默认分组信息失败");
		}

		//获取设备信息
		InitEquipmentResult equipment = equipmentService.initEquipment(divideGroup);
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
			@ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "address", value = "地址", required = false, dataType = "String", paramType = "query")
	})
	@PostMapping(value = "query" ,produces = "application/json;charset=UTF-8")
	public StarNodeWrappedResult query (String name, String ip,
										@RequestParam(value = "pageIndex", defaultValue = "1")String  pageIndex,
										@RequestParam(value = "pageSize", defaultValue = "10")String  pageSize) {
		StarNodeResultObject starNodeResultObject = equipmentService.queryPage(name, ip, pageIndex, pageSize);
		if (starNodeResultObject == null) {
			log.error("查询设备信息失败");
			return StarNodeWrappedResult.failWrapedResult("查询设备信息失败");
		}
		return StarNodeWrappedResult.successWrapedResult("查询设备信息失败", starNodeResultObject);
	}

	@ApiOperation(value="设置设备分组失败", notes="设置设备分组失败")
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
			log.error("设置设备分组失败， 分组信息为空为空");
			return StarNodeWrappedResult.failWrapedResult("设置设备分组失败， 分组信息为空为空");
		}

		Collection<Equipment> equipment = null;
		try {
			equipment = equipmentService.setDivideGroup(ids, groupId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("设置设备: {} 分组失败", equipment);
			return StarNodeWrappedResult.failWrapedResult("设置设备分组失败", equipment);
		}
		return StarNodeWrappedResult.successWrapedResult("设置设备分组失败", equipment);
	}


}