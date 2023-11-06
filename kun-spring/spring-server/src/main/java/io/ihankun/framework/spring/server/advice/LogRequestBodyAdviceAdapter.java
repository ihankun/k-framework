package io.ihankun.framework.spring.server.advice;

import io.ihankun.framework.log.context.TraceLogContext;
import io.ihankun.framework.spring.server.utils.RequestLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

@Slf4j
@ControllerAdvice
public class LogRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {

    @Autowired
    HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(MethodParameter methodParameter, Type type,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        long contentLength = inputMessage.getHeaders().getContentLength();
        String url = MDC.get(TraceLogContext.REQUEST_URI);
        //根据 contentLength 和 日志级别 判断是否打印全量日志
        RequestLogUtil.logWithContentLength(contentLength, url, body);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}
