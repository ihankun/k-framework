package io.hankun.framework.ai.mcp.app.caller.impl;

import io.hankun.framework.ai.core.entity.HttpResult;
import io.hankun.framework.ai.core.http.KHttpClient;
import io.hankun.framework.ai.core.util.PathUtil;
import io.hankun.framework.ai.mcp.app.auth.KGatewayAuthUtil;
import io.hankun.framework.ai.mcp.app.caller.IKHttpCaller;
import io.hankun.framework.ai.mcp.app.config.KAiHttpConfig;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import io.hankun.framework.core.context.sys.DomainContext;
import io.hankun.framework.core.context.user.LoginUserContext;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * @description:
 * @className: KGateWayHttpCaller
 * @createAt: 2026/1/4 11:40
 * @author: hankun
 */
@Slf4j
@ConditionalOnMissingBean(KLoadBalanceCaller.class)
@Component
public class KGateWayHttpCaller implements IKHttpCaller {

    private final KHttpClient kHttpClient;

    private final KAiHttpConfig kAiHttpConfig;

    public KGateWayHttpCaller(KAiHttpConfig kAiHttpConfig) {
        this.kAiHttpConfig = kAiHttpConfig;
        this.kHttpClient = new KHttpClient(kAiHttpConfig.getClient());
    }

    @Override
    public HttpResult get(String path, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        StringBuilder urlBuilder = new StringBuilder();
        if (!CollectionUtils.isEmpty(params)) {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                String query = "";
                if (value != null) {
                    query = value.toString();
                }
                builder.append(key).append("=").append(query).append("&");
                urlBuilder.append(key).append("=").append(urlEncode(query)).append("&");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        String queryString = builder.toString();
        String auth = KGatewayAuthUtil.generateAuthorization(queryString, null,
                LoginUserContext.get(), kAiHttpConfig);
        String url = buildUrl(path);
        log.info("GET请求地址:{}", url + "?" + queryString);
        log.info("GET请求参数:{}", queryString);
        Request request = kHttpClient.get(url + "?" + urlBuilder, requestBuilder -> {
            buildHeader(requestBuilder, auth);
        });
        HttpResult result = kHttpClient.call(request, true);
        log.info("GET请求结果:{}", result);
        return result;
    }

    @NotNull
    private String buildUrl(String path) {
        if (StringUtils.hasText(kAiHttpConfig.getGatewayUrl())) {
            return PathUtil.mergePath(kAiHttpConfig.getGatewayUrl(), path);
        } else {
            return PathUtil.mergePath("https://" + DomainContext.get(), path);
        }
    }


    @Override
    public HttpResult post(String path, String requestBody) {
        String auth = KGatewayAuthUtil.generateAuthorization(null, requestBody,
                LoginUserContext.get(), kAiHttpConfig);
        String url = buildUrl(path);
        log.info("POST请求地址:{}", url);
        log.info("POST请求参数:{}", requestBody);
        Request request = kHttpClient.post(url, requestBody, requestBuilder -> {
            buildHeader(requestBuilder, auth);
        });
        HttpResult result = kHttpClient.call(request, true);
        log.info("POST请求结果:{}", result);
        return result;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }


    private static void buildHeader(Request.Builder requestBuilder, String auth) {
        KContext kContext = KContextHolder.get();
        requestBuilder.addHeader("Authorization", auth);
        requestBuilder.addHeader("Content-Type", "application/json");
        requestBuilder.addHeader("domain", ENCODER.encodeToString(kContext.getDomain().getBytes()));
        requestBuilder.addHeader("Host", kContext.getDomain());
        requestBuilder.addHeader("gray", kContext.getGray());
        requestBuilder.addHeader("gateway", "app");
    }

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
}
