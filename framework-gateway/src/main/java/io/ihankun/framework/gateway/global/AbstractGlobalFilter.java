package io.ihankun.framework.gateway.global;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.core.response.ResponseResult;
import io.ihankun.framework.gateway.context.RequestAttrNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public abstract class AbstractGlobalFilter {

    public Mono<Void> flush(ServerWebExchange exchange, ResponseResult msg) {
        ServerHttpResponse resp = exchange.getResponse();
        byte[] bits = JSON.toJSONString(msg).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = resp.bufferFactory().allocateBuffer(bits.length).write(bits);
        resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        Long traceId = exchange.getAttribute(RequestAttrNames.TRACE_ID);

        log.info("请求中断,traceId={},url={},result={}", traceId, exchange.getRequest().getURI(), msg);
        return resp.writeWith(Mono.just(buffer));
    }
}
