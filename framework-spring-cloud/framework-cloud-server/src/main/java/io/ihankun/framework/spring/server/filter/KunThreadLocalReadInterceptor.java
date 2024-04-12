package io.ihankun.framework.spring.server.filter;

import io.ihankun.framework.common.utils.spring.SpringHelpers;
import io.ihankun.framework.spring.server.filter.thread.IKunThreadLocalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;

/**
 * @author hankun
 */
@Configuration
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class KunThreadLocalReadInterceptor implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                SpringHelpers.context().getBeansOfType(IKunThreadLocalFilter.class)
                        .values().stream()
                        .sorted(Comparator.comparing(IKunThreadLocalFilter::order))
                        .forEach(item -> item.readHeader(request));
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                SpringHelpers.context().getBeansOfType(IKunThreadLocalFilter.class).values().forEach(item -> item.clearThreadLocal());
            }
        });
    }
}
