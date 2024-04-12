package io.ihankun.framework.spring.server.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hankun
 */
@Slf4j
@Component
public class KunCorsFilter implements Filter, ApplicationContextAware {

    private static final String ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ALLOW_HEADERS = "Access-Control-Allow-Headers";

    private ApplicationContext context;

    private static final String KEY_CORS = "ihankun.spring.cors";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        if (this.context == null) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        //获取跨域请求开关
        String cors = this.context.getEnvironment().getProperty(KEY_CORS, "false");

        //如果为true，则开启跨域，其余均为关闭状态
        if (!Boolean.TRUE.toString().equals(cors)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String origin = request.getHeader("Origin");
        if (log.isDebugEnabled()) {
            log.debug("CorsFilter.doFilter.start,origin={}", origin);
        }

        HttpServletResponse res = (HttpServletResponse) servletResponse;
        if (StringUtils.isEmpty(res.getHeader(ALLOW_CREDENTIALS))) {
            res.addHeader(ALLOW_CREDENTIALS, "true");
        }
        if (StringUtils.isEmpty(res.getHeader(ALLOW_ORIGIN))) {
            res.addHeader(ALLOW_ORIGIN, origin);
        }
        if (StringUtils.isEmpty(res.getHeader(ALLOW_METHODS))) {
            res.addHeader(ALLOW_METHODS, "*");
        }
        if (StringUtils.isEmpty(res.getHeader(ALLOW_HEADERS))) {
            String str = "Login-User,Authorization,DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With," +
                    "If-Modified-Since," +
                    "Cache-Control,Content-Type";
            res.addHeader(ALLOW_HEADERS, str + "," + str.toLowerCase());
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
