package io.ihankun.framework.spring.server.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author hankun
 */
@Slf4j
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    public RestTemplateLoggingInterceptor() {
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 打印请求日志
        if (log.isDebugEnabled()) {
            log.debug("RestTemplate 请求URI: {}, 请求参数: {}", request.getURI(), new String(body, StandardCharsets.UTF_8));
            log.debug("RestTemplate Headers: {}", request.getHeaders());
        }
        ClientHttpResponse response = execution.execute(request, body);
        // 打印响应日志
        if (log.isDebugEnabled()) {
            ClientHttpResponse responseCopy = new BufferingClientHttpResponseWrapper(response);
            log.debug("RestTemplate 响应结果: {}", IOUtils.toString(responseCopy.getBody(), String.valueOf(StandardCharsets.UTF_8)));
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
            return this.response.getStatusCode();
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return this.response.getRawStatusCode();
        }

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
