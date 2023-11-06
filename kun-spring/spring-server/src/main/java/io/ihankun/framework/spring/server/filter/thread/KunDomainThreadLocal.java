package io.ihankun.framework.spring.server.filter.thread;

import feign.RequestTemplate;
import io.ihankun.framework.common.context.DomainContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@Slf4j
@Component
public class KunDomainThreadLocal implements IKunThreadLocalFilter {

    Base64.Encoder encoder = Base64.getEncoder();
    Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public String name() {
        return "domain";
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public String writeHeader(RequestTemplate template) {
        //从上下文中取出域名信息，放置到Http请求Header中进行透传
        String domain = DomainContext.get();
        log.info("DomainThreadLocal.writeHeader,domain={}", domain);
        if (!StringUtils.isEmpty(domain)) {
            domain = encoder.encodeToString(domain.getBytes());
            template.header(DomainContext.DOMAIN_HEADER_NAME, domain);
        }
        return domain;
    }

    @Override
    public String readHeader(HttpServletRequest request) {
        //域名信息获取
        String domain = request.getHeader(DomainContext.DOMAIN_HEADER_NAME);
        if (!StringUtils.isEmpty(domain)) {
            domain = new String(decoder.decode(domain));
            DomainContext.mock(domain);
            log.info("DomainThreadLocal.readHeader,domain={}", domain);
        }
        return domain;
    }

    @Override
    public void clearThreadLocal() {
        DomainContext.clear();
    }
}
