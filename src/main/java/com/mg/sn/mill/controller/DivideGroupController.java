package com.mg.sn.mill.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.mill.model.entity.Currency;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.model.entity.Equipment;
import com.mg.sn.mill.model.entity.Wallet;
import com.mg.sn.mill.service.ICurrencyService;
import com.mg.sn.mill.service.IDivideGroupService;
import com.mg.sn.mill.service.IEquipmentService;
import com.mg.sn.mill.service.IWalletService;
import com.mg.sn.utils.Enum.DefaultStatusEnum;
import com.mg.sn.utils.annotation.Auth;
import com.mg.sn.utils.baseController.StarNodeBaseController;
import com.mg.sn.utils.redis.RedisUtil;
import com.mg.sn.utils.result.StarNodeResultObject;
import com.mg.sn.utils.result.StarNodeWrappedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hcy
 * @since 2019-08-12
 */
@Api(description = "分组模块")
@Auth
@RestController
@RequestMapping("/groupController")
public class DivideGroupController extends StarNodeBaseController {

    private static final Logger log = LoggerFactory.getLogger(DivideGroupController.class);

    @Resource
    private ICurrencyService currencyService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IDivideGroupService divideGroupService;

    @Autowired
    private IEquipmentService equipmentService;

    @Autowired
    private RedisUtil redisUtil;

    //默认状态key
    private static final String DEFAULT_STATUS_REDIS_KEY = "DEFAULT_STATUS";

    @ApiOperation(value="保存分组信息", notes="保存分组信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currencyId", value = "币种ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "miningPool", value = "矿池", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "walletId", value = "钱包ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "save" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult save (String name, String currencyId, String miningPool, String walletId, String remark) {

        //获取用户ID
        String userId = getUserId();

        if (StringUtils.isEmpty(name)) {
            log.error("保存分组信息失败， 分组名称为空");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 分组名称为空");
        }

        if (StringUtils.isEmpty(currencyId)) {
            log.error("保存分组信息失败， 币种为空");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 币种为空");
        }

        if (StringUtils.isEmpty(miningPool)) {
            log.error("保存分组信息失败， 矿池为空");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 矿池为空");
        }

        if (StringUtils.isEmpty(walletId)) {
            log.error("保存分组信息失败， 钱包信息为空");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 钱包为空");
        }

        //判断币种是否存在
        Currency currency = currencyService.getById(currencyId);
        if (currency == null) {
            log.error("保存分组信息失败， 币种不存在");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 币种不存在");
        }

        //判断钱包是否存在
        Wallet walletInfo = walletService.getById(walletId);
        if (walletInfo == null) {
            log.error("保存分组信息失败， 钱包信息不存在");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 钱包信息不存在");
        }

        //判断币种是否指定的同类型钱包地址
        if (Integer.parseInt(currencyId) != walletInfo.getCurrencyId()) {
            log.error("保存分组信息失败， 币种类型和钱包地址不匹配");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 币种类型和钱包地址不匹配");
        }

        //判断分组名称是否重复
        List<DivideGroup> groupListByName = divideGroupService.queryFroVali("", name, "", "", "");
        if (groupListByName.size() > 0) {
            log.error("保存分组信息失败， 分组名称已存在");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 分组名称已存在");
        }

        //判断币种和钱包地址是否重复
        List<DivideGroup> divideGroupList = divideGroupService.queryFroVali("", "", currencyId, walletId, "");
        if (divideGroupList.size() > 0) {
            log.error("保存分组信息失败， 币种和钱包地址已经存在");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 分组中币种和钱包地址已经存在");
        }

        DivideGroup result = null;
        try {
            result = divideGroupService.save(name, currencyId, miningPool, walletId, remark, userId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存分组信息失败， 添加分组入库失败");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 添加分组入库失败");
        }

        //保存成功
        return StarNodeWrappedResult.successWrapedResult("保存分组信息成功", result);
    }

    @ApiOperation(value="查询分组信息", notes="查询分组信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currencyId", value = "币种ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currencyName", value = "币种名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "miningPool", value = "矿池", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "walletId", value = "钱包ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "walletName", value = "钱包名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createUserId", value = "创建用户ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createUserName", value = "创建用户名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "query" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult query (String name, String currencyId, String currencyName, String miningPool, String walletId, String walletName, String remark, String createUserId, String createUserName,
                                        @RequestParam(value = "pageIndex", defaultValue = "1")String  pageIndex,
                                        @RequestParam(value = "pageSize", defaultValue = "10")String  pageSize) {

        StarNodeResultObject starNodeResultObject = divideGroupService.queryList(name, currencyId, currencyName, miningPool, walletId, walletName, remark, createUserId, createUserName, pageIndex, pageSize);
        if (starNodeResultObject == null) {
            log.error("查询分组信息失败");
            return StarNodeWrappedResult.failWrapedResult("查询分组信息失败");
        }

        return StarNodeWrappedResult.successWrapedResult("查询分组信息成功", starNodeResultObject);
    }

    @ApiOperation(value="查询所有分组信息", notes="查询所有分组信息")
    @PostMapping(value = "queryAll" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult queryAll () {
        List<DivideGroup> list = null;
        try {
            list = divideGroupService.list();
        } catch (Exception e) {
            log.error("查询所有分组信息失败");
        }
        return StarNodeWrappedResult.successWrapedResult("查询所有分组信息成功", list);
    }

    @ApiOperation(value="分组选择钱包地址", notes="分组选择钱包地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currencyId", value = "币种ID", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "selectAddress" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult selectAddress (String currencyId) {

        if (StringUtils.isEmpty(currencyId)) {
            log.error("分组选择钱包地址，币种为空");
            return StarNodeWrappedResult.failWrapedResult("分组选择钱包地址，币种为空");
        }

        //判断币种是否存在
        Currency currency = currencyService.getById(currencyId);
        if (currency == null) {
            log.error("分组选择钱包地址失败，币种不存在");
            return StarNodeWrappedResult.failWrapedResult("分组选择钱包地址失败，币种不存在");
        }

        List<Wallet> result = walletService.queryForVali("", currencyId, "");

        return StarNodeWrappedResult.successWrapedResult("分组选择钱包地址成功", result);
    }

    @ApiOperation(value="设置默认状态", notes="设置默认状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分组ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "defaultStatus", value = "默认状态(1:启动;2:未启动)", required = true, dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "setDefaultStatus" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult setDefaultStatus (String id, String defaultStatus) {

        //获取用户ID
        String userId = getUserId();

        if (StringUtils.isEmpty(id)) {
            log.error("设置默认状态失败， 钱包分组ID为空");
            return StarNodeWrappedResult.failWrapedResult("设置默认状态失败， 钱包分组ID为空");
        }

        if (StringUtils.isEmpty(defaultStatus)) {
            log.error("设置默认状态失败， 默认状态为空");
            return StarNodeWrappedResult.failWrapedResult("设置默认状态失败， 默认状态为空");
        }

        //默认状态没有改动
        DivideGroup listbyId = divideGroupService.getById(id);
        if (com.mg.sn.utils.common.StringUtils.stringEquals (listbyId.getDefaultStatus(), defaultStatus)) {
            log.error("设置默认状态失败， 默认状态没有更改");
            return StarNodeWrappedResult.failWrapedResult("设置默认状态失败， 默认状态没有更改");
        }


        //状态开启
        if (com.mg.sn.utils.common.StringUtils.stringEquals(DefaultStatusEnum.OPEN.getCode(), defaultStatus)) {
            //判断分组名称是否重复
            List<DivideGroup> groupValiList = divideGroupService.queryFroVali("", "", "", "", defaultStatus);
            if (groupValiList.size() > 0) {
                log.error("设置默认状态失败, 默认状态只能允许存在一个");
                return StarNodeWrappedResult.failWrapedResult("设置默认状态失败, 默认状态只能允许存在一个");
            }

            long defaultStatusSet = redisUtil.sSet(DEFAULT_STATUS_REDIS_KEY, DefaultStatusEnum.OPEN.getCode());
            //value放入set，返回值为0证明已经存在
            if (defaultStatusSet == 0) {
                log.error("设置默认状态失败, 已经存在启动分组");
                return StarNodeWrappedResult.failWrapedResult("设置默认状态失败, 已经存在启动分组");
            }

            DivideGroup result = null;
            try {
                result = divideGroupService.updateDefaultStatus(id, defaultStatus, userId);
            } catch (Exception e) {
                e.printStackTrace();
                //删除默认状态
                redisUtil.setRemove(DEFAULT_STATUS_REDIS_KEY, DefaultStatusEnum.OPEN.getCode());
                log.error("设置默认状态失败", e);
                return StarNodeWrappedResult.failWrapedResult("设置默认状态失败");
            }

            return StarNodeWrappedResult.successWrapedResult("设置默认状态成功", result);
        }

        //状态关闭
        DivideGroup result = null;
        try {
            result = divideGroupService.updateDefaultStatus(id, defaultStatus, userId);
            if (redisUtil.hasKey(DEFAULT_STATUS_REDIS_KEY)) {
                //删除默认状态
                redisUtil.setRemove(DEFAULT_STATUS_REDIS_KEY, DefaultStatusEnum.OPEN.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("设置默认状态失败", e);
            return StarNodeWrappedResult.failWrapedResult("设置默认状态失败");
        }

        return StarNodeWrappedResult.successWrapedResult("设置默认状态成功", result);
    }

    @ApiOperation(value="删除分组", notes="删除分组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分组ID", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "delete" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult delete (String id) {

        if (StringUtils.isEmpty(id)) {
            log.error("删除分组失败, ID不能为空");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, ID不能为空");
        }

        //分组是否存在
        DivideGroup listById = divideGroupService.getById(id);
        if (listById  == null) {
            log.error("删除分组失败, 分组信息不存在");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 分组信息不存在");
        }

        //TODO
        //使用mysql行锁锁住分组数据

        //验证分组ID是否已经被设备使用
        List<Equipment> idValiList = null;
        try {
            idValiList = equipmentService.queryForVali(id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除分组失败, 查询分组使用情况失败");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 查询分组使用情况失败");
        }

        if (idValiList.size() > 0) {
            log.error("删除分组失败, 设备正在使用该分组");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 设备正在使用该分组");
        }

        boolean result = listById.deleteById();
        if (!result) {
            log.error("删除分组失败");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败");
        }

        //删除分组成功
        return StarNodeWrappedResult.successWrapedResult("删除分组成功", result);
    }

    @ApiOperation(value="编辑分组", notes="编辑分组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分组ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currencyId", value = "币种ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "walletId", value = "钱包ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "update" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult update (String id, String name, String currencyId, String walletId, String remark){

        //获取用户ID
        String userId = getUserId();

        if (StringUtils.isEmpty(id)) {
            log.error("编辑分组信息失败， 分组ID为空");
            return StarNodeWrappedResult.failWrapedResult("编辑分组信息失败， 分组ID为空");
        }

        if (StringUtils.isEmpty(name)) {
            log.error("编辑分组信息失败， 分组名称为空");
            return StarNodeWrappedResult.failWrapedResult("编辑分组信息失败， 分组名称为空");
        }

        if (StringUtils.isEmpty(currencyId)) {
            log.error("编辑分组信息失败， 币种为空");
            return StarNodeWrappedResult.failWrapedResult("编辑分组信息失败， 币种为空");
        }

        if (StringUtils.isEmpty(walletId)) {
            log.error("编辑分组信息失败， 钱包信息为空");
            return StarNodeWrappedResult.failWrapedResult("编辑分组信息失败， 钱包为空");
        }

        //分组是否存在
        DivideGroup listById = divideGroupService.getById(id);
        if (listById  == null) {
            log.error("编辑分组失败, 分组信息不存在");
            return StarNodeWrappedResult.failWrapedResult("编辑分组失败, 分组信息不存在");
        }

        //判断币种是否存在
        Currency currency = currencyService.getById(currencyId);
        if (currency == null) {
            log.error("编辑分组信息失败， 币种不存在");
            return StarNodeWrappedResult.failWrapedResult("编辑分组信息失败， 币种不存在");
        }

        //判断钱包是否存在
        Wallet walletInfo = walletService.getById(walletId);
        if (walletInfo == null) {
            log.error("保存分组信息失败， 钱包信息不存在");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 钱包信息不存在");
        }

        //判断币种是否指定的同类型钱包地址
        if (Integer.parseInt(currencyId) != walletInfo.getCurrencyId()) {
            log.error("保存分组信息失败， 币种类型和钱包地址不匹配");
            return StarNodeWrappedResult.failWrapedResult("保存分组信息失败， 币种类型和钱包地址不匹配");
        }

        //验证名称是否重复
        List<DivideGroup> nameValiList = divideGroupService.queryFroVali(id, name, "", "", "");
        if (nameValiList.size() > 0) {
            log.error("删除分组失败, 分组名称已经存在请修改");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 分组名称已经存在请修改");
        }

        //验证币种和地址是否重复
        List<DivideGroup> cuAndWaValiList = divideGroupService.queryFroVali(id, "", currencyId, walletId, "");
        if (cuAndWaValiList.size() > 0) {
            log.error("删除分组失败, 币种和地址已经存在请修改");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 币种和地址已经存在请修改");
        }

        //TODO
        //使用mysql行锁锁住分组数据

        //验证分组ID是否已经被设备使用
        List<Equipment> idValiList = null;
        try {
            idValiList = equipmentService.queryForVali(id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除分组失败, 查询分组信息失败");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 查询分组信息失败");
        }

        if (idValiList.size() > 0) {
            log.error("删除分组失败, 设备正在使用该分组");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 设备正在使用该分组");
        }

        DivideGroup result = null;
        try {
            result = divideGroupService.update(id, name, currencyId, walletId, remark, userId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除分组失败, 更新分组数据失败");
            return StarNodeWrappedResult.failWrapedResult("删除分组失败, 更新分组数据失败");
        }

        //更新分组成功
        return StarNodeWrappedResult.successWrapedResult("更新分组成功", result);
    }



}
