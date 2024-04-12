package io.ihankun.framework.spring.server.filter.thread;

import cn.hutool.core.lang.UUID;
import feign.RequestTemplate;
import io.ihankun.framework.common.id.IdGenerator;
import io.ihankun.framework.log.context.TraceLogContext;
import io.ihankun.framework.spring.server.utils.RequestLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hankun
 */
@Slf4j
@Component
public class KunTraceIdThreadLocal implements IKunThreadLocalFilter {

    private final String GET = String.valueOf(RequestMethod.GET);
    private final String POST = String.valueOf(RequestMethod.POST);

    @Override
    public String name() {
        return "trace-id";
    }

    @Override
    public Integer order() {
        return -1;
    }

    @Override
    public String writeHeader(RequestTemplate template) {
        //从上下文中取出traceId
        String traceId = TraceLogContext.get();
        if (StringUtils.isEmpty(traceId)) {
            traceId = IdGenerator.ins().generator().toString();
        }
        template.header(TraceLogContext.TRACE_HEADER_NAME, traceId);
        log.info("TraceIdThreadLocal.writeHeader,traceId={}", traceId);
        return traceId;
    }


    @Override
    public String readHeader(HttpServletRequest request) {
        return logTraceId(request);
    }

    /**
     * 获取header中的traceId，并放入ThreadContext中
     *
     * @param request
     * @return
     */
    private String logTraceId(HttpServletRequest request) {
        String uri = String.valueOf(request.getRequestURI());
        //先从header获取traceId
        String traceId = request.getHeader(TraceLogContext.TRACE_HEADER_NAME);
        if (StringUtils.isEmpty(traceId)) {
            // 使用Skywalking获取traceId。
            traceId = TraceContext.traceId();
        }
        //如果获取不到traceId，则尝试主动生成一个ID
        if (StringUtils.isEmpty(traceId)) {
            //traceId = IdGenerator.ins().generator().toString();
            traceId = UUID.fastUUID().toString();
            log.info("TraceIdThreadLocal.readHeader.generator,traceId={}", traceId);
        }
        //放入log4j2的线程上下文中，打印日志时会用到
        TraceLogContext.set(traceId);
        MDC.putCloseable(TraceLogContext.REQUEST_URI, uri);
        log.info("TraceIdThreadLocal.readHeader,traceId={}", traceId);

        logRequest(request);
        return traceId;
    }

    /**
     * 打印请求参数，1、get请求，2、post请求但是request.getQueryString()有值的
     *
     * @param request
     */
    private void logRequest(HttpServletRequest request) {
        String uri = String.valueOf(request.getRequestURI());
        //1、获取get请求参数
        if (GET.equals(request.getMethod())) {
            RequestLogUtil.log("请求地址:{},请求参数：{}", uri, request.getQueryString());
        }
        //2、post类型的请求,但是可能在url里传递参数
        if (POST.equals(request.getMethod()) && request.getQueryString() != null) {
            RequestLogUtil.log("请求地址:{},请求参数：{}", uri, request.getQueryString());
        }
    }

    @Override
    public void clearThreadLocal() {
        TraceLogContext.reset();
    }

}
