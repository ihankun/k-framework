package io.ihankun.framework.spring.server.filter.thread;

import feign.RequestTemplate;
import io.ihankun.framework.core.context.GrayContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hankun
 */
@Slf4j
@Component
public class KunGrayThreadLocal implements IKunThreadLocalFilter {

    @Override
    public String name() {
        return "gray";
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public String writeHeader(RequestTemplate template) {
        //从上下文中取出灰度标识gray
        String gray = GrayContext.get();
        if (!StringUtils.isEmpty(gray)) {
            template.header(GrayContext.GRAY_HEADER_NAME, gray);
        }
        log.info("GrayThreadLocal.writeHeader,gray={}", gray);
        return gray;
    }

    @Override
    public String readHeader(HttpServletRequest request) {
        //获取灰度标识
        String gray = request.getHeader(GrayContext.GRAY_HEADER_NAME);
        if (!StringUtils.isEmpty(gray)) {
            GrayContext.mock(gray);
            log.info("GrayThreadLocal.readHeader,gray={}", gray);
        }
        return gray;
    }

    @Override
    public void clearThreadLocal() {
        GrayContext.clear();
    }
}
