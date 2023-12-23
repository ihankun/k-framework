package io.ihankun.framework.network.http;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

/**
 * @author hankun
 */
@Slf4j
public class RestTemplateHttp implements HttpInterface {

    @Resource
    private RestTemplate restTemplate;

    @Override
    public <T> T get(String url, ParameterizedTypeReference<T> typeReference){
        return getHttp(url, typeReference, null);
    }

    @Override
    public <T> T get(String url, ParameterizedTypeReference<T> typeReference, Map<String, String> headers) {
        HttpHeaders httpHeaders = setHeader(headers);
        return getHttp(url, typeReference, httpHeaders);
    }

    @Override
    public <T> T get(String url, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders) {
        return getHttp(url, typeReference, httpHeaders);
    }

    @Override
    public <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference) {
        return postHttp(url, data, typeReference, null);
    }

    @Override
    public <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference, Map<String, String> headers) {
        HttpHeaders httpHeaders = setHeader(headers);
        return postHttp(url, data, typeReference, httpHeaders);
    }

    @Override
    public <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders) {
        return postHttp(url, data, typeReference, httpHeaders);
    }

    private  <T> T getHttp(String url, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders){
        if(httpHeaders == null) {
            httpHeaders = setHeader();
        }
        HttpEntity<String> formEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<T> response;
        if(restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        response = restTemplate.exchange(url, HttpMethod.GET, formEntity, typeReference);
        return response.getBody();
    }

    private  <T> T postHttp(String url, Object data, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders) {
        if(httpHeaders == null) {
            httpHeaders = setHeader();
        }
        String dataJson = JSONUtil.toJsonStr(data);
        HttpEntity<String> formEntity = new HttpEntity<>(dataJson, httpHeaders);
        ResponseEntity<T> response;
        if(restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        response = restTemplate.exchange(url, HttpMethod.POST, formEntity, typeReference);
        return response.getBody();
    }

    private HttpHeaders setHeader(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    private HttpHeaders setHeader(Map<String, String> headers){
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::set);
        return httpHeaders;
    }


}
