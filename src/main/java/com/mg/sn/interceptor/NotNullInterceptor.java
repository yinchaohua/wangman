package com.mg.sn.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.utils.annotation.NotNull;
import com.mg.sn.utils.annotation.Param;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class NotNullInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //判断请求是否是方法级别
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NotNull notNullAnnotation = handlerMethod.getMethod().getAnnotation(NotNull.class);
        
        //判断是否存在注解
        if (notNullAnnotation == null) {
            return true;
        }

        Param[] params = notNullAnnotation.value();
        if (params.length == 0) {
            return true;
        }

        for (Param param : params) {
            //参数KEY
            String paramKey = param.paramKey();
            //返回值信息
            String message = param.message();

        }
        return true;
    }
}
