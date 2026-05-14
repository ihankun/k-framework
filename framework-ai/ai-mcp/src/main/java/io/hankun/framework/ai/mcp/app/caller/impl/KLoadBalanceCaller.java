package io.hankun.framework.ai.mcp.app.caller.impl;

import io.hankun.framework.ai.common.entity.HttpResult;
import io.hankun.framework.ai.mcp.app.caller.IKHttpCaller;
import io.hankun.framework.commons.http.KHttpClientFactory;
import io.hankun.framework.commons.http.KHttpResponse;
import io.hankun.framework.commons.http.KWebClient;
import io.hankun.framework.core.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @description:
 * @className: KLoadBalanceCaller
 * @createAt: 2026/1/4 14:05
 * @author: hankun
 */
@Slf4j
@ConditionalOnBooleanProperty(name = "k.ai.http.enableLoadBalance", matchIfMissing = true)
@Component
public class KLoadBalanceCaller implements IKHttpCaller {

    private final KWebClient kWebClient;


    public KLoadBalanceCaller(KHttpClientFactory kHttpClientFactory) {
        this.kWebClient = new KWebClient(kHttpClientFactory.newLoadBalancedWebClient("mcp"));
    }

    @Override
    public HttpResult get(String path, Map<String, Object> params) {
        path = getPath(path);
        Mono<KHttpResponse<Object>> mono = kWebClient.getWithResponseResult(path, params,
                new ParameterizedTypeReference<>() {
                });
        return HttpResult.of(mono.block());
    }

    @Override
    public HttpResult post(String path, String requestBody) {
        path = getPath(path);
        Mono<ResponseEntity<ResponseResult<Object>>> entity = kWebClient.post(path, requestBody, new Consumer<HttpHeaders>() {
            @Override
            public void accept(HttpHeaders httpHeaders) {
                httpHeaders.add("Content-Type", "application/json");
            }
        }).toEntity(new ParameterizedTypeReference<>() {
        });
        Mono<KHttpResponse<Object>> mono = entity.map(KHttpResponse::ofK);
        return HttpResult.of(mono.block());
    }

    private static @NotNull String getPath(String path) {
        path = "http://" + path;
        return path;
    }
}
