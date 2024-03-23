package io.ihankun.framework.spring.server.advice;

import io.ihankun.framework.common.v1.enums.ResponseLevelEnum;
import io.ihankun.framework.common.v1.response.ResponseResult;
import io.ihankun.framework.log.context.TraceLogContext;
import io.ihankun.framework.spring.server.error.AdviceErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author hankun
 */
@ConditionalOnProperty(prefix = "project.separate", value = {"enable"}, havingValue = "true", matchIfMissing = true)
@ControllerAdvice
@Order(Integer.MAX_VALUE - 1)
public class ResponseBodyAdvice implements org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice{

    @Value("${spring.application.name:none}")
    private String service;

    private static final List<String> IGNORE_URL = Arrays.asList(new String[]{"swagger-resources", "api-docs", "actuator"});

    private static final String HTTP_STATUS_CODE = "status";

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String url = httpRequest.getRequestURL().toString();
        boolean ignore = IGNORE_URL.stream().anyMatch(item -> url.contains(item));
        if (ignore) {
            return body;
        }

        if (body == null) {
            ResponseResult<Object> success = ResponseResult.success();
            if (!StringUtils.isEmpty(TraceLogContext.get())) {
                success.setTraceId(TraceLogContext.get());
            }
            success.setService(service);
            success.setLevel(ResponseLevelEnum.INFO.name().toLowerCase());
            return success;
        }

        if (body instanceof ResponseResult) {
            ResponseResult result = (ResponseResult) body;
            if (result.isDecorate()) {
                if (!StringUtils.isEmpty(TraceLogContext.get())) {
                    result.setTraceId(TraceLogContext.get());
                }
                result.setService(service);
                return body;
            }
            return result.getData();
        }

        if (body instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) body;
            Integer sucessStatus = new Integer(HttpStatus.OK.value());
            if (map.containsKey(HTTP_STATUS_CODE) && !sucessStatus.equals(map.get(HTTP_STATUS_CODE))) {
                ResponseResult<LinkedHashMap> error = ResponseResult.error(map, AdviceErrorCode.CLIENT_ERROR);
                if (!StringUtils.isEmpty(TraceLogContext.get())) {
                    error.setTraceId(TraceLogContext.get());
                }
                error.setService(service);
                error.setLevel(ResponseLevelEnum.WARN.name().toLowerCase());
                return error;
            }
        }

        ResponseResult<Object> success = ResponseResult.success(body);
        if (!StringUtils.isEmpty(TraceLogContext.get())) {
            success.setTraceId(TraceLogContext.get());
        }
        success.setService(service);
        return success;
    }
}
