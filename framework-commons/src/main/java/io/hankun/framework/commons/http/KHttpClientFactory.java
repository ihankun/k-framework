package io.hankun.framework.commons.http;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * @description:
 * @className: KHttpClientFactory
 * @createAt: 2025/12/22 16:32
 * @author: hankun
 */
public interface KHttpClientFactory {

    WebClient.Builder withHttpClient(WebClient.Builder builder, String code);

    WebClient newLoadBalancedWebClient(WebClient.Builder builder);

    default WebClient newLoadBalancedWebClient(String code) {
        WebClient.Builder builder = withHttpClient(WebClient.builder(), code);
        return newLoadBalancedWebClient(builder);
    }

    WebClient newWebClient(WebClient.Builder builder);

    default WebClient newWebClient(String code) {
        WebClient.Builder builder = withHttpClient(WebClient.builder(), code);
        return newWebClient(builder);
    }

}
