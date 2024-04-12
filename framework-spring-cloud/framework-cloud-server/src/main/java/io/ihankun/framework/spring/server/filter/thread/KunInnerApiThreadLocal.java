package io.ihankun.framework.spring.server.filter.thread;

import feign.RequestTemplate;
import feign.Target;
import io.ihankun.framework.core.utils.spring.SpringHelpers;
import io.ihankun.framework.log.context.TraceLogApiInfoContext;
import io.ihankun.framework.log.entity.ApiInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hankun
 */
@Slf4j
@Component
public class KunInnerApiThreadLocal implements IKunThreadLocalFilter {

    private String applicationName = null;

    @Override
    public String name() {
        return "Upstream";
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public String writeHeader(RequestTemplate template) {

        if (applicationName == null) {
            applicationName = SpringHelpers.context().getEnvironment().getProperty("spring.application.name");
        }

        String url = template.url();

        TraceLogApiInfoContext.reset();
        Target<?> target = template.feignTarget();
        if (target != null) {
            TraceLogApiInfoContext.set(new ApiInfo(target.name(), url));
            log.info("FeignFilter.ApiInfo.set,targetServiceName={},url={}", target.name(), url);
        }

        log.info("InnerApiThreadLocal.writeHeader,Upstream={}", applicationName);
        return url;
    }

    @Override
    public String readHeader(HttpServletRequest request) {
        return null;
    }

    @Override
    public void clearThreadLocal() {

    }
}
