package com.mg.sn.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.utils.annotation.Auth;
import com.mg.sn.utils.annotation.NotNull;
import com.mg.sn.utils.annotation.Param;
import com.mg.sn.utils.redis.RedisUtil;
import com.mg.sn.utils.result.StarNodeWrappedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        //判断请求是否是方法级别
        if (!(handler instanceof HandlerMethod)) {
            log.info("handler不是方法级别");
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth authAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Auth.class);

        //判断是否存在注解
        if (authAnnotation == null) {
            log.info("注解不存在");
            return true;
        }

        //获取token信息
//        String token = request.getHeader("token");
//        if (StringUtils.isEmpty(token)) {
//            try {
//                responsePrintWriter(response, "验证身份失败, token不存在", false);
//                return false;
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error("response返回对象异常");
//                return false;
//            }
//        }

//        boolean isExist = redisUtil.hasKey(token);
//        if (!isExist) {
//            try {
//                responsePrintWriter(response, "用户登陆超时，请重新登陆", false);
//                return false;
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error("response返回对象异常");
//                return false;
//            }
//        }
        return true;
    }

    /**
     * response返回值
     * @param response
     * @param mes  返回信息
     * @param isSuccess  是否成功
     * @throws Exception
     */
    private void responsePrintWriter (HttpServletResponse response, String mes, boolean isSuccess) throws Exception {
        PrintWriter writer = response.getWriter();
        String resultObject = returnMes(isSuccess, mes);
        writer.append(resultObject);
    }

    /**
     * 返回对象
     * @param isSuccess  是否成功
     * @param mes   返回信息
     * @return
     */
    private String returnMes (boolean isSuccess, String mes) {
        //返回对象
        StarNodeWrappedResult starNodeWrappedResult = new StarNodeWrappedResult(isSuccess, "", mes, "", "");
        return JSONObject.toJSONString(starNodeWrappedResult);
    }
}
