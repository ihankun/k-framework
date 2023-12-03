package io.ihankun.framework.common.http.v1;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import java.util.Map;

/**
 * @author hankun
 */
public interface HttpInterface {

    /**
     * get请求
     * @param url 请求路径
     * @param typeReference 类型
     * @author hankun
     * @return <T>
     */
    <T> T get(String url, ParameterizedTypeReference<T> typeReference);

    /**
     * get请求
     * @param url 请求路径
     * @param typeReference 类型
     * @param headers 头文件
     * @author hankun
     * @return <T>
     */
    <T> T get(String url, ParameterizedTypeReference<T> typeReference, Map<String, String> headers);

    /**
     * get请求
     * @param url 请求路径
     * @param typeReference 类型
     * @param httpHeaders 头文件
     * @author hankun
     * @return <T>
     */
    <T> T get(String url, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders);

    /**
     * post请求
     * @param url 请求路径
     * @param data 入参
     * @param typeReference 类型
     * @author hankun
     * @return <T>
     */
    <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference);

    /**
     * post请求
     * @param url 请求路径
     * @param data 入参
     * @param typeReference 类型
     * @param headers 请求头
     * @author hankun
     * @return <T>
     */
    <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference, Map<String, String> headers);

    /**
     * post请求
     * @param url 请求路径
     * @param data 入参
     * @param typeReference 类型
     * @param httpHeaders 请求头
     * @author hankun
     * @return <T>
     */
    <T> T post(String url, Object data, ParameterizedTypeReference<T> typeReference, HttpHeaders httpHeaders);
}
