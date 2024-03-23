package io.ihankun.framework.spring.server.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import io.ihankun.framework.common.v1.context.*;
import io.ihankun.framework.common.v1.id.IdGenerator;
import io.ihankun.framework.log.context.TraceLogApiInfoContext;
import io.ihankun.framework.log.context.TraceLogContext;
import io.ihankun.framework.log.entity.ApiInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

/**
 * @author hankun
 */
@Configuration
@ConditionalOnClass(FeignClient.class)
@Slf4j
public class KunFeignFilter implements RequestInterceptor {

    @Value("${spring.application.name}")
    public String upstreamServerName;

    Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public void apply(RequestTemplate template) {

        String url = template.url();

        //从上下文中取出用户信息，放置到Http请求Header中进行透传
        LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo != null) {
            debug("FeignFilter.loginUser.header.set,url={},userInfo={}", url, userInfo);
            String encode = new String(encoder.encode(userInfo.toJson().getBytes()));
            template.header(LoginUserContext.LOGIN_USER_KEY, encode);
        } else {
            debug("FeignFilter.loginUser.header.null,url={}", url);
        }

        //从上下文中取出域名信息，放置到Http请求Header中进行透传
        String domain = DomainContext.get();
        if (!StringUtils.isEmpty(domain)) {
            debug("FeignFilter.domain.header.set,url={},domain={}", url, domain);
            domain = encoder.encodeToString(domain.getBytes());
            template.header(DomainContext.DOMAIN_HEADER_NAME, domain);
        } else {
            debug("FeignFilter.domain.header.null,url={}", url);
        }

        //从上下文中取出业务首次发生时间
        String time = BusinessTimeContext.get();
        if (!StringUtils.isEmpty(time)) {
            debug("FeignFilter.time.header.set,url={},time={}", url, time);
            time = encoder.encodeToString(time.getBytes());
            template.header(BusinessTimeContext.BUSINESS_START_TIME_HEADER_NAME, time);
        } else {
            debug("FeignFilter.time.header.null,url={}", url);
        }

        //从上下文中取出traceId
        String traceId = TraceLogContext.get();
        if (StringUtils.isEmpty(traceId)) {
            traceId = IdGenerator.ins().generator().toString();
            debug("FeignFilter.trace.header.init,url={},traceId={}", url, traceId);
        }
        debug("FeignFilter.trace.header.set,url={},traceId={}", url, traceId);
        template.header(TraceLogContext.TRACE_HEADER_NAME, traceId);

        //从上下文中取出灰度标识gray
        String gray = GrayContext.get();
        if (!StringUtils.isEmpty(gray)) {
            debug("FeignFilter.gray.header.init,url={},gray={}", url, gray);
            template.header(GrayContext.GRAY_HEADER_NAME, gray);
        } else {
            debug("FeignFilter.gray.header.set,url={},gray={}", url, gray);
        }
        TraceLogApiInfoContext.reset();
        Target<?> target = template.feignTarget();
        if (target != null) {
            TraceLogApiInfoContext.set(new ApiInfo(target.name(), url));
            log.info("FeignFilter.ApiInfo.set,targetServiceName={},url={}", target.name(), url);
        }
    }

    private void debug(String msg, Object... param) {
        if (log.isDebugEnabled()) {
            log.debug(msg, param);
        }
    }
}
