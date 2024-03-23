package io.ihankun.framework.spring.server.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.ihankun.framework.common.v1.utils.spring.SpringHelpers;
import io.ihankun.framework.spring.server.filter.thread.IKunThreadLocalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

/**
 * @author hankun
 */
@Configuration
@ConditionalOnClass(FeignClient.class)
@Slf4j
public class KunFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        StringBuilder logBuilder = new StringBuilder();

        SpringHelpers.context().getBeansOfType(IKunThreadLocalFilter.class).values().stream().sorted(Comparator.comparing(IKunThreadLocalFilter::order)).forEach(item -> {
            String returnStr = item.writeHeader(template);
            logBuilder.append(item.name()).append("=").append(returnStr).append(",");
        });

        String path = template.path();
        String url = template.feignTarget().url();

        log.info("Feign.interceptor,target={},path={},header={}", url, path, logBuilder);
    }
}
