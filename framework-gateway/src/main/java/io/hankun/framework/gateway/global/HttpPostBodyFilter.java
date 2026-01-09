package io.hankun.framework.gateway.global;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import io.hankun.framework.gateway.context.RequestAttrNames;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author hankun
 */
@Slf4j
@Component
public class HttpPostBodyFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -9;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();


        String method = request.getMethod().name();
        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

        log.info("HttpPostBodyFilter.filter.start,url={},method={},contentType={}", url, method, contentType);

        //文件上传忽略
        if (StringUtils.isNotEmpty(contentType) && contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return chain.filter(exchange);
        }

        if (HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method)) {
            try {
                return DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            String bodyString = new String(bytes, StandardCharsets.UTF_8);

                            exchange.getAttributes().put(RequestAttrNames.REQUEST_BODY, bodyString);

                            DataBufferUtils.release(dataBuffer);
                            Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                                DataBuffer buffer = exchange.getResponse().bufferFactory().allocateBuffer(bytes.length).write(bytes);
                                return Mono.just(buffer);
                            });

                            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                    exchange.getRequest()) {
                                @Override
                                public Flux<DataBuffer> getBody() {
                                    return cachedFlux;
                                }
                            };
                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        });
            } catch (Exception e) {
                log.error("网关捕获参数信息异常", e);
                return chain.filter(exchange);
            }
        }


        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        log.info("HttpPostBodyFilter.filter.start2,url={},method={},queryParams={}", url, method, queryParams);

        if (MapUtil.isEmpty(queryParams)) {
            return chain.filter(exchange);
        }

        String json = JSON.toJSONString(queryParams);
        exchange.getAttributes().put(RequestAttrNames.REQUEST_BODY, json);
        return chain.filter(exchange);
    }


}
