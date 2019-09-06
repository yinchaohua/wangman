package com.mg.sn.frame;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("HttpServletRequestWrapperFilter")
public class HttpServletRequestWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        ServletRequest requestWrapper = null;

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            //设置编码
            httpRequest.setCharacterEncoding("UTF-8");
            httpResponse.setCharacterEncoding("UTF-8");

            //遇上POST才对request进行封装
//            String methodType = httpRequest.getMethod();
//            //上传文件时同样不进行包装
//            String servletPath = httpRequest.getRequestURI().toString();
//            if ("POST".equals(methodType) && !servletPath.contains("/material/upload")) {
//                requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);
//            }

//            if (null == requestWrapper) {
//                chain.doFilter(request, httpResponse);
//            } else {
//                chain.doFilter(requestWrapper, httpResponse);
//            }
            chain.doFilter(request, httpResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
