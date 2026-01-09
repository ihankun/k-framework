package io.hankun.framework.springcloud.server.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.hankun.framework.core.utils.spring.SpringHelpers;
import io.hankun.framework.springcloud.server.filter.thread.IKunThreadLocalFilter;
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
public class KunThreadLocalWriteInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        SpringHelpers.context().getBeansOfType(IKunThreadLocalFilter.class).
                values().stream().
                sorted(Comparator.comparing(IKunThreadLocalFilter::order)).
                forEach(item -> item.writeHeader(template));
    }
}
