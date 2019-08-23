package com.mg.sn.mill.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.mill.model.entity.Currency;
import com.mg.sn.mill.model.entity.DivideGroup;
import com.mg.sn.mill.model.entity.Wallet;
import com.mg.sn.mill.service.ICurrencyService;
import com.mg.sn.mill.service.IDivideGroupService;
import com.mg.sn.mill.service.IWalletService;
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

import org.springframework.stereotype.Controller;
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
 * @since 2019-08-09
 */
@Api(description = "钱包模块")
@RestController
@RequestMapping("/walletController")
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private IWalletService walletService;

    @Autowired
    private ICurrencyService currencyService;

    @Autowired
    private IDivideGroupService divideGroupService;

    @ApiOperation(value="保存钱包信息", notes="保存钱包信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currencyId", value = "币种ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "address", value = "地址", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "save" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult save (String name, String currencyId, String address) {

        if (StringUtils.isEmpty(name)) {
            log.error("保存钱包信息失败，钱包名称为空");
            return StarNodeWrappedResult.failWrapedResult("保存钱包信息失败，钱包名称为空");
        }

        if (StringUtils.isEmpty(currencyId)) {
            log.error("保存钱包信息失败，币种为空");
            return StarNodeWrappedResult.failWrapedResult("保存钱包信息失败，币种为空");
        }

        if (StringUtils.isEmpty(address)) {
            log.error("保存钱包信息失败，钱包地址为空");
            return StarNodeWrappedResult.failWrapedResult("保存钱包信息失败，钱包地址为空");
        }

        //判断币种是否存在
        Currency currency = currencyService.getById(currencyId);
        if (currency == null) {
            log.error("保存钱包信息失败，币种不存在");
            return StarNodeWrappedResult.failWrapedResult("保存钱包信息失败，币种不存在");
        }

        //验证钱包名称是否存在
        List<Wallet> nameValiList = walletService.queryForVali(name, "", "");
        if (nameValiList.size() > 0) {
            log.error("保存钱包信息失败，钱包名称已经存在");
            return StarNodeWrappedResult.failWrapedResult("保存钱包信息失败，钱包名称已经存在");
        }

        //验证币种和地址是否存在
        List<Wallet> walletValiList = walletService.queryForVali("", currencyId, address);
        if (walletValiList.size() > 0) {
            log.error("保存钱包信息失败，币种和地址已经存在");
            return StarNodeWrappedResult.failWrapedResult("保存钱包信息失败，币种和地址已经存在");
        }

        Wallet result = null;
        try {
            result = walletService.save(name, currencyId, address);
        } catch (Exception e) {
            log.error("保存钱包信息失败");
            e.printStackTrace();
            return StarNodeWrappedResult.failWrapedResult("保存钱包信息失败");
        }

        return StarNodeWrappedResult.successWrapedResult("保存钱包信息成功", result);
    }

    @ApiOperation(value="查询钱包信息", notes="查询钱包信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currencyId", value = "币种ID", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currencyName", value = "币种名称", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "address", value = "地址", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "query" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult query (String name, String currencyId, String currencyName, String address,
                                        @RequestParam(value = "pageIndex", defaultValue = "1")String  pageIndex,
                                        @RequestParam(value = "pageSize", defaultValue = "10")String  pageSize) {

        StarNodeResultObject result = walletService.queryList(name, currencyId, currencyName, address, pageIndex, pageSize);
        if (result == null) {
            log.error("查询钱包信息失败");
            return StarNodeWrappedResult.failWrapedResult("查询钱包信息失败");
        }

        return StarNodeWrappedResult.successWrapedResult("查询钱包信息成功", result);
    }

    @ApiOperation(value="删除钱包信息", notes="删除钱包信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "钱包ID", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "delete" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult delete (String id) {

        //钱包ID不能为空
        if (StringUtils.isEmpty(id)) {
            log.error("删除钱包信息失败，钱包ID为空");
            return StarNodeWrappedResult.failWrapedResult("删除钱包信息失败，钱包ID为空");
        }

        //已经分组的钱包不能删除
        List<DivideGroup> divideGroupList = divideGroupService.queryFroVali("", "", "","", id);
        if (divideGroupList.size() > 0) {
            log.error("删除钱包信息失败，钱包已存在分组");
            return StarNodeWrappedResult.failWrapedResult("删除钱包信息失败，钱包已存在分组");
        }

        boolean result = walletService.removeById(id);
        if (!result){
            log.error("删除钱包信息失败, 钱包ID {}", id);
            return StarNodeWrappedResult.failWrapedResult("删除钱包信息失败");
        }
        //删除成功
        return StarNodeWrappedResult.successWrapedResult("删除钱包信息成功");
    }
}
