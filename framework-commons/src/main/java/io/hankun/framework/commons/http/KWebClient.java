package io.hankun.framework.commons.http;

import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import io.hankun.framework.core.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @description:
 * @className: KWebClient
 * @createAt: 2025/12/2 08:32
 * @author: hankun
 */
@Slf4j
public record KWebClient(WebClient webClient) {

    public static final String CONTEXT = "context";

    /**
     * GET 请求
     *
     * @param url          url
     * @param params       参数
     * @param responseType 响应类型
     * @param <T>          响应类型
     * @return 响应
     */
    public <T> Mono<KHttpResponse<T>> get(String url, Map<String, Object> params, Class<T> responseType) {
        Mono<ResponseEntity<T>> entity = get(url, params)
                .toEntity(responseType);
        return entity.map(KHttpResponse::of);
    }

    /**
     * GET 请求
     *
     * @param url          url
     * @param responseType 响应类型
     * @param <T>          响应类型
     * @return 响应
     */
    public <T> Mono<KHttpResponse<T>> get(String url, Map<String, Object> params, ParameterizedTypeReference<T> responseType) {
        Mono<ResponseEntity<T>> entity = get(url, params)
                .toEntity(responseType);
        return entity.map(KHttpResponse::of);
    }

    /**
     * GET 请求
     *
     * @param url          url
     * @param params       参数
     * @param responseType 响应类型
     * @param <T>          响应类型
     * @return 响应
     */
    public <T> Mono<KHttpResponse<T>> getWithResponseResult(String url, Map<String, Object> params,
                                                            ParameterizedTypeReference<ResponseResult<T>> responseType) {
        Mono<ResponseEntity<ResponseResult<T>>> entity = get(url, params)
                .toEntity(responseType);
        return entity.map(KHttpResponse::ofK);
    }

    public WebClient.@NotNull ResponseSpec get(String url, Map<String, Object> params) {
        return get(url, params, headers -> {
        });
    }

    public WebClient.@NotNull ResponseSpec get(String url, Map<String, Object> params, Consumer<HttpHeaders> headersConsumer) {
        log.debug("发送GET请求，url: {}, params: {}", url, params);
        return webClient.get()
                .uri(url, new Function<UriBuilder, URI>() {
                    @Override
                    public URI apply(UriBuilder uriBuilder) {
                        for (Map.Entry<String, Object> entry : params.entrySet()) {
                            uriBuilder.queryParam(entry.getKey(), entry.getValue());
                        }
                        return uriBuilder.build();
                    }
                })
                .headers(headersConsumer)
                .attribute(CONTEXT, getContext())
                .retrieve();
    }


    public <T> Flux<T> getFlux(String url, Map<String, Object> params, ParameterizedTypeReference<T> responseType) {
        WebClient.ResponseSpec responseSpec = get(url, params);
        return responseSpec.bodyToFlux(responseType);
    }

    /**
     * POST 请求
     *
     * @param url          url
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @param <T>          响应类型
     * @return 响应
     */
    public <T> Mono<KHttpResponse<T>> post(String url, Object requestBody, Class<T> responseType) {
        Mono<ResponseEntity<T>> entity = post(url, requestBody)
                .toEntity(responseType);
        return entity.map(KHttpResponse::of);
    }

    /**
     * POST 请求
     *
     * @param url          url
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @param <T>          响应类型
     * @return 响应
     */
    public <T> Mono<KHttpResponse<T>> post(String url, Object requestBody, ParameterizedTypeReference<T> responseType) {
        Mono<ResponseEntity<T>> entity = post(url, requestBody)
                .toEntity(responseType);
        return entity.map(KHttpResponse::of);
    }

    /**
     * POST 请求
     *
     * @param url          url
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @param <T>          响应类型
     * @return 响应
     */
    public <T> Mono<KHttpResponse<T>> postWithResponseResult(String url, Object requestBody,
                                                             ParameterizedTypeReference<ResponseResult<T>> responseType) {
        Mono<ResponseEntity<ResponseResult<T>>> entity = post(url, requestBody)
                .toEntity(responseType);
        return entity.map(KHttpResponse::ofK);
    }

    public <T> Flux<T> postFlux(String url, Object requestBody, ParameterizedTypeReference<T> responseType) {
        WebClient.ResponseSpec responseSpec = post(url, requestBody);
        return responseSpec.bodyToFlux(responseType);
    }

    public WebClient.@NotNull ResponseSpec post(String url, Object requestBody) {
        return post(url, requestBody, headers -> {
        });
    }

    public WebClient.@NotNull ResponseSpec post(String url, Object requestBody, Consumer<HttpHeaders> headersConsumer) {
        log.debug("发送POST请求，url: {}, body: {}", url, requestBody);
        return webClient.post()
                .uri(url)
                .headers(headersConsumer)
                .bodyValue(requestBody)
                .attribute(CONTEXT, getContext())
                .retrieve();
    }

    private static KContext getContext() {
        return KContextHolder.get();
    }
}
