package com.mg.sn.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.frame.BodyReaderHttpServletRequestWrapper;
import com.mg.sn.mill.controller.CurrencyController;
import com.mg.sn.utils.annotation.NotNull;
import com.mg.sn.utils.annotation.Param;
import com.mg.sn.utils.common.TypeConvert;
import com.mg.sn.utils.result.StarNodeWrappedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Component
public class NotNullInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(NotNullInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //判断请求是否是方法级别
        if (!(handler instanceof HandlerMethod)) {
            log.info("handler不是方法级别");
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        NotNull notNullAnnotation = handlerMethod.getMethod().getAnnotation(NotNull.class);
        //判断是否存在注解
        if (notNullAnnotation == null) {
            log.info("注解不存在");
            return true;
        }

        Param[] params = notNullAnnotation.value();
        //判断注解参数是否为空
        if (params.length == 0) {
            log.info("注解参数为空");
            return true;
        }

        //解析传入参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null) {
            try {
                responsePrintWriter(response, "传入参数为空", false);
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("response返回对象异常");
                return false;
            }
        }

        for (Param param : params) {
            //参数KEY
            String paramKey = param.paramKey();
            //返回值信息
            String message = param.message();

            if (StringUtils.isEmpty(paramKey) || StringUtils.isEmpty(message)) {
                try {
                    responsePrintWriter(response, "参数是否为空判断失败, 注解对象不能为空", false);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("response返回对象异常");
                    return false;
                }
            }

            //获取参数
            String paramValue = org.apache.commons.lang.StringUtils.join(parameterMap.get(paramKey));
            if (StringUtils.isEmpty(paramValue)) {
                try {
                    responsePrintWriter(response, message + "不能为空", false);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("response返回对象异常");
                    return false;
                }
            }
        }
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
