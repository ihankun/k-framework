package io.hankun.framework.gateway.filter.impl;

import cn.hutool.core.lang.UUID;
import io.hankun.framework.core.response.ResponseResult;
import io.hankun.framework.gateway.context.RequestAttrNames;
import io.hankun.framework.gateway.entity.FilterResult;
import io.hankun.framework.gateway.entity.GatewayParam;
import io.hankun.framework.gateway.filter.IRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author hankun
 */
@Component
@Slf4j
public class TraceIdFilter implements IRequestFilter {

    private static final String TRACE_ID_HEADER_NAME = "traceId";
    private static final String IGNORED_TRACE = "Ignored_Trace";

    @Override
    public Integer order() {
        return -1;
    }

    @Override
    public ResponseResult<FilterResult> filter(ServerWebExchange exchange, GatewayParam param) {

        //改为从header中获取openRestry中生成的traceId
        HttpHeaders headers = param.getHeaders();
        String traceId =  headers.getFirst(TRACE_ID_HEADER_NAME);
        log.info("get traceId from headers:{}",traceId);

        if (StringUtils.isEmpty(traceId) || IGNORED_TRACE.equals(traceId)) {
            traceId = UUID.fastUUID().toString();
        }

        FilterResult result = new FilterResult();
        result.addReqHeader(RequestAttrNames.TRACE_ID, traceId, false);

        log.info("跟踪记录ID,traceId={},url={},param={}", traceId, param.getUrl(), param);
        return ResponseResult.success(result);
    }
}
