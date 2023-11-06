package io.ihankun.framework.spring.server.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.ihankun.framework.common.utils.SpringHelpers;
import io.ihankun.framework.spring.server.filter.thread.IKunThreadLocalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

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
