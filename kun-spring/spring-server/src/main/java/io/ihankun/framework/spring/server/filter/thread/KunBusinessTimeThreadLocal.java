package io.ihankun.framework.spring.server.filter.thread;

import feign.RequestTemplate;
import io.ihankun.framework.common.context.BusinessTimeContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@Slf4j
@Component
public class KunBusinessTimeThreadLocal implements IKunThreadLocalFilter {

    Base64.Encoder encoder = Base64.getEncoder();
    Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public String name() {
        return "start-time";
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public String writeHeader(RequestTemplate template) {
        //从上下文中取出业务首次发生时间
        String time = BusinessTimeContext.get();
        if (!StringUtils.isEmpty(time)) {
            time = encoder.encodeToString(time.getBytes());
            template.header(BusinessTimeContext.BUSINESS_START_TIME_HEADER_NAME, time);
        }

        log.info("BusinessTimeThreadLocal.writeHeader,time={}", time);
        return time;
    }

    @Override
    public String readHeader(HttpServletRequest request) {
        //获取业务发生时间
        String time = request.getHeader(BusinessTimeContext.BUSINESS_START_TIME_HEADER_NAME);
        if (!StringUtils.isEmpty(time)) {
            time = new String(decoder.decode(time));
            BusinessTimeContext.mock(time);
            log.info("BusinessTimeThreadLocal.readHeader,time={}", time);
        }
        return time;
    }

    @Override
    public void clearThreadLocal() {
        BusinessTimeContext.clear();
    }
}
