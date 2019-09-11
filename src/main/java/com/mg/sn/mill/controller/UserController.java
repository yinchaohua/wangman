package com.mg.sn.mill.controller;

import com.mg.sn.ConfigProperties.LoginConfig;
import com.mg.sn.mill.model.entity.User;
import com.mg.sn.mill.service.UserService;
import com.mg.sn.utils.annotation.NotNull;
import com.mg.sn.utils.annotation.Param;
import com.mg.sn.utils.baseController.StarNodeBaseController;
import com.mg.sn.utils.common.StringUtils;
import com.mg.sn.utils.redis.RedisUtil;
import com.mg.sn.utils.result.InitEquipmentResult;
import com.mg.sn.utils.result.StarNodeWrappedResult;
import com.mg.sn.utils.security.SecurityUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  用户操作
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */
@Api(description = "登陆")
@RestController
@RequestMapping("/userController")
public class UserController extends StarNodeBaseController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginConfig loginConfig;

    private static final String LOGIN_ERROR_NUM_KEY = "LOGIN_ERROR_NUM_KEY";


    @ApiOperation(value="登陆", notes="登陆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = false, dataType = "String", paramType = "query")
    })
    @NotNull({
            @Param(paramKey = "account", message = "账号"),
            @Param(paramKey = "password", message = "密码")
    })
    @PostMapping(value = "login" ,produces = "application/json;charset=UTF-8")
    public StarNodeWrappedResult login (String account, String password) {

        //传入密码加密
//        String encryPassword = SecurityUtils.MD5Encode(password, "UTF-8", "MD5");

        //查询用户
        List<User> userList = userService.queryForLogin(account, password);

        //验证用户
        if (userList.size() == 0) {
            //增加错误次数
            redisUtil.incr(LOGIN_ERROR_NUM_KEY, 1);

            log.error("登陆失败， 用户名或密码错误");
            return StarNodeWrappedResult.failWrapedResult("登陆失败， 用户名或密码错误");
        }

        //登陆成功(清空错误次数)
        if (redisUtil.hasKey(LOGIN_ERROR_NUM_KEY)) {
            redisUtil.set(LOGIN_ERROR_NUM_KEY, 0);
        }
        //生成token
        String token = StringUtils.uuid();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userList.get(0).getId());
        map.put("account", account);
        redisUtil.set(token, map, loginConfig.getTokenExpireTime());

        return StarNodeWrappedResult.successWrapedResult("登陆成功", token);
    }



}