package com.mg.sn.mill.controller;

import com.mg.sn.mill.model.entity.Currency;
import com.mg.sn.mill.service.ICurrencyService;
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
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hcy
 * @since 2019-08-13
 */
@Api(description = "币种")
@RestController
@RequestMapping("/currencyController")
public class CurrencyController {

    @Autowired
    private ICurrencyService currencyService;

    private static final Logger log = LoggerFactory.getLogger(CurrencyController.class);

    @ApiOperation(value="查询币种信息", notes="查询币种信息")
    @PostMapping(value = "queryAll" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult queryAll () {
        List<Currency> result = null;
        try{
            result = currencyService.list();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询币种信息失败", e);
            return StarNodeWrappedResult.failWrapedResult("查询币种信息失败");
        }
        //保存成功
        return StarNodeWrappedResult.successWrapedResult("查询币种信息成功", result);
    }

}
