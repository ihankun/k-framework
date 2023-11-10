package io.ihankun.framework.spring.server.limit.tomcat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import io.ihankun.framework.common.context.DomainContext;
import io.ihankun.framework.common.context.LoginUserContext;
import io.ihankun.framework.common.context.LoginUserInfo;
import io.ihankun.framework.common.error.impl.CommonErrorCode;
import io.ihankun.framework.common.response.ResponseResult;
import io.ihankun.framework.spring.server.filter.thread.KunDomainThreadLocal;
import io.ihankun.framework.spring.server.filter.thread.KunLoginUserThreadLocal;
import io.ihankun.framework.spring.server.limit.IRateSnapshotStrategy;
import io.ihankun.framework.spring.server.limit.engine.RateElement;
import io.ihankun.framework.spring.server.limit.engine.RateSnapshotEngine;
import io.ihankun.framework.spring.server.limit.properties.RateProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author hankun
 */
@Slf4j
@Component
public class TomcatFilter implements Filter {
    @Resource
    RateSnapshotEngine engine;

    @Resource
    RateProperties properties;

    @Resource
    KunLoginUserThreadLocal kunLoginUserThreadLocal;

    @Resource
    KunDomainThreadLocal kunDomainThreadLocal;

    IRateSnapshotStrategy snapshotStrategy;

    /**
     * 当前请求队列
     */
    private final Queue<RateElement> queue = new ConcurrentLinkedQueue<>();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        snapshotStrategy = new IRateSnapshotStrategy() {
            @Override
            public String businessKey() {
                return "Tomcat";
            }

            @Override
            public RateProperties.Config config() {
                return properties.getTomcat();
            }

            @Override
            public List<RateElement> snapshot() {
                return new ArrayList<>(queue);
            }
        };
        engine.load(snapshotStrategy);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //关闭状态，不进行后续逻辑判断
        if (!properties.getTomcat().isEnable()) {
            chain.doFilter(request, response);
            return;
        }

        RateElement element = null;

        //限流逻辑尝试处理
        try {

            HttpServletRequest servletRequest = (HttpServletRequest) request;
            //读取请求头中的用户
            kunLoginUserThreadLocal.readHeader(servletRequest);
            kunDomainThreadLocal.readHeader(servletRequest);
            //限流逻辑
            LoginUserInfo userInfo = LoginUserContext.get();
            String userId = userInfo == null ? "" : (userInfo.getUserId() == null ? "" : userInfo.getUserId().toString());
            String domain = DomainContext.get() == null ? "" : DomainContext.get();

            String url = servletRequest.getRequestURI();
            element = new RateElement(url, domain, userId);

            RateSnapshotEngine.RateElementLimitedWrap limited = engine.limited(element, snapshotStrategy);
            if (limited != null) {
                //释放时间
                String expireTime = DateUtil.formatDateTime(new Date(limited.getExpireTime()));
                response.setCharacterEncoding(CharsetUtil.UTF_8);
                response.setContentType(ContentType.APPLICATION_JSON.toString());
                response.getWriter().println(ResponseResult.error(CommonErrorCode.TOMCAT_RATE_LIMITED, expireTime));
                return;
            }

        } catch (Exception e) {
            log.error("自适应限流逻辑异常,记录错误并放行", e);
        }

        //发起原始请求
        try {
            queue.add(element);
            chain.doFilter(request, response);
        } finally {
            if (element != null) {
                queue.remove(element);
            }
        }
    }
}
