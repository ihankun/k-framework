package io.ihankun.framework.spring.server.advice;

import io.ihankun.framework.common.response.ResponseResult;
import io.ihankun.framework.spring.server.utils.RequestLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ConditionalOnProperty(prefix = "project.separate", value = {"enable"}, havingValue = "true", matchIfMissing = true)
@ControllerAdvice
@Order(Integer.MAX_VALUE)
public class LogResponseBodyAdvice implements org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice{

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseResult) {
            RequestLogUtil.log("请求地址:{},返回值:{}", request.getURI(), body);
        }
        return body;
    }
}
