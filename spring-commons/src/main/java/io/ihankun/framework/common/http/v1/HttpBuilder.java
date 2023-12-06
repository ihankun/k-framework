package io.ihankun.framework.common.http.v1;

import io.ihankun.framework.common.http.v1.enums.HttpType;
import io.ihankun.framework.common.http.v1.impl.RestTemplateHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import java.util.Map;

/**
 * @author hankun
 */
@Slf4j
public class HttpBuilder {

    public static final class HttpBuilderHolder {
        public static HttpType HTTP_TYPE = null;
        public static HttpBuilder HTTP_BUILDER = null;
    }

    private final HttpInterface httpInterface;

    public static HttpBuilder ins(){
        return ins(HttpType.R_COMM);
    }

    public static HttpBuilder ins(HttpType type){
        HttpBuilderHolder.HTTP_TYPE = type;
        if(HttpBuilderHolder.HTTP_BUILDER == null) {
            HttpBuilderHolder.HTTP_BUILDER = new HttpBuilder(HttpBuilderHolder.HTTP_TYPE);
        }
        return HttpBuilderHolder.HTTP_BUILDER;
    }

    public HttpBuilder(HttpType type) {
        HttpInterface http;
        if(type.equals(HttpType.R_COMM)) {
            http = new RestTemplateHttp();
        }else {
            http = null;
        }
        this.httpInterface = http;
    }

    public <T> T get(String url, ParameterizedTypeReference<T> typeReference){
        return httpInterface.get(url, typeReference);
    }

    public <T> T get(String url, ParameterizedTypeReference<T> typeReference, Map<String, String> headers){
        return httpInterface.get(url, typeReference, headers);
    }

    public <T> T get(String url, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders){
        return httpInterface.get(url, typeReference, httpHeaders);
    }

    public <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference){
        return httpInterface.post(url, data, typeReference);
    }

    public <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference, Map<String, String> headers){
        return httpInterface.post(url, data, typeReference, headers);
    }

    public <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders){
        return httpInterface.post(url, data, typeReference, httpHeaders);
    }
}
