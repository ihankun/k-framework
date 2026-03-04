package io.hankun.framework.springboot.config;

import io.hankun.framework.core.context.DomainContext;
import io.hankun.framework.core.context.GrayContext;
import io.hankun.framework.core.context.LoginUserContext;
import io.hankun.framework.core.context.LoginUserInfo;
import io.hankun.framework.core.utils.spring.SpringHelpers;
import io.hankun.framework.log.context.TraceLogContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * RestTemplate 日志拦截器
 *
 * @author hankun
 */
@Slf4j
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {
    Base64.Encoder encoder = Base64.getEncoder();

    public RestTemplateLoggingInterceptor() {
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            //请求头中添加traceId
            request.getHeaders().add(TraceLogContext.TRACE_HEADER_NAME, TraceLogContext.get());
            //出口网关的系统日志里需要 restTemplate调用的发起方服务名称、domain
            String applicationName = SpringHelpers.getPropertiesWithCache("spring.application.name", "none");
            //request.getHeaders().add(TraceLogContext.UPSTREAM_HEADER_NAME, applicationName);
            //request.getHeaders().add(TraceLogContext.DOMAIN_HEADER_NAME, DomainContext.getCurrentDomain());

            String gray = request.getHeaders().getFirst(GrayContext.GRAY_HEADER_NAME);
            if (StringUtils.isEmpty(gray)) {
                request.getHeaders().add(GrayContext.GRAY_HEADER_NAME, GrayContext.get());
            }

            String domain = request.getHeaders().getFirst(DomainContext.DOMAIN_HEADER_NAME);
            if (StringUtils.isEmpty(domain)) {
                if (!StringUtils.isEmpty(DomainContext.get())) {
                    request.getHeaders().add(DomainContext.DOMAIN_HEADER_NAME, Base64.getEncoder().encodeToString(DomainContext.get().getBytes(StandardCharsets.UTF_8)));
                }
            }

            String loginUserKey = request.getHeaders().getFirst(LoginUserContext.LOGIN_USER_KEY);
            if (StringUtils.isEmpty(loginUserKey)) {
                LoginUserInfo userInfo = LoginUserContext.get();
                if (userInfo != null) {
                    String encode = new String(encoder.encode(userInfo.toJson().getBytes()));
                    request.getHeaders().add(LoginUserContext.LOGIN_USER_KEY, encode);
                }
            }
        } catch (Exception exception) {
            log.error("RestTemplate.exception", exception);
        }

        // 打印请求日志
        if (log.isDebugEnabled()) {
            log.debug("RestTemplate 请求URI: {}, 请求参数: {}", request.getURI(), new String(body, StandardCharsets.UTF_8));
            log.debug("RestTemplate Headers: {}", request.getHeaders());
        }
        ClientHttpResponse response = execution.execute(request, body);
        // 打印响应日志
        if (log.isDebugEnabled()) {
            ClientHttpResponse responseCopy = new BufferingClientHttpResponseWrapper(response);
            log.debug("RestTemplate 响应结果: {}", IOUtils.toString(responseCopy.getBody(), StandardCharsets.UTF_8));
            return responseCopy;
        }
        return response;
    }

    /**
     * 响应内容备份
     */
    final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse response;

        private byte[] body;


        BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }


        @Override
        public HttpStatus getStatusCode() throws IOException {
            return HttpStatus.valueOf(this.response.getStatusCode().value());
        }

//        @Override
//        public int getRawStatusCode() throws IOException {
//            return this.response.getStatusCode().value();
//        }

        @Override
        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }

        @Override
        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        @Override
        public void close() {
            this.response.close();
        }
    }
}
