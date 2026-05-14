package io.hankun.framework.commons.http;

import io.hankun.framework.core.response.ResponseResult;
import org.springframework.http.ResponseEntity;

/**
 * @description:
 * @className: KHttpResponse
 * @createAt: 2026/1/4 14:11
 * @author: hankun
 */
public record KHttpResponse<T>(boolean success,
                               String message, T data, String traceId) {


    public static <T> KHttpResponse<T> success(T data) {
        return success(data, "");
    }

    public static <T> KHttpResponse<T> success(T data, String traceId) {
        return new KHttpResponse<>(true, "接口调用成功", data, traceId);
    }

    public static <T> KHttpResponse<T> success(String message, T data, String traceId) {
        return new KHttpResponse<>(true, message, data, traceId);
    }

    public static <T> KHttpResponse<T> fail(String message) {
        return new KHttpResponse<>(false, message, null, "");
    }

    public static <T> KHttpResponse<T> fail(String message, String traceId) {
        return new KHttpResponse<>(false, message, null, traceId);
    }


    public static <T> KHttpResponse<T> of(ResponseEntity<T> responseEntity) {
        if (responseEntity == null) {
            return KHttpResponse.fail("无响应体", "");
        }
        if (responseEntity.getBody() == null) {
            return KHttpResponse.fail("无响应体", responseEntity.getHeaders().getFirst("traceId"));
        }
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return KHttpResponse.success(responseEntity.getBody(), responseEntity.getHeaders().getFirst("traceId"));
        } else {
            return KHttpResponse.fail("请求接口失败：" + responseEntity.getStatusCode(), responseEntity.getHeaders().getFirst("traceId"));
        }
    }

    public static <T> KHttpResponse<T> ofK(ResponseEntity<ResponseResult<T>> responseEntity) {
        if (responseEntity == null) {
            return KHttpResponse.fail("无响应体", "");
        }
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ResponseResult<T> body = responseEntity.getBody();
            if (body != null) {
                if (body.isSuccess()) {
                    return KHttpResponse.success(body.getMessage(), body.getData(), body.getTraceId());
                } else {
                    return KHttpResponse.fail(body.getMessage(), body.getTraceId());
                }
            } else {
                return KHttpResponse.fail("无响应体", responseEntity.getHeaders().getFirst("traceId"));
            }
        } else {
            return KHttpResponse.fail("请求接口失败：" + responseEntity.getStatusCode(), responseEntity.getHeaders().getFirst("traceId"));
        }
    }
}
