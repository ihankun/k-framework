package io.hankun.framework.ai.common.entity;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.commons.http.KHttpResponse;
import org.springframework.util.ObjectUtils;

/**
 * @description:
 * @className: HttpResult
 * @createAt: 2025/6/5 18:00
 * @author: hankun
 */
public record HttpResult(boolean success, boolean exception, String message, String data, String traceId) {

    public static HttpResult success(String data) {
        return success(data, "");
    }

    public static HttpResult success(String data, String traceId) {
        if (ObjectUtils.isEmpty(data)) {
            return new HttpResult(true, false, "工具调用成功，结果为空", "", traceId);
        }
        return new HttpResult(true, false, "工具调用成功", data, traceId);
    }

    public static HttpResult success(String message, String data, String traceId) {
        if (ObjectUtils.isEmpty(data)) {
            return new HttpResult(true, false, "工具调用成功，结果为空", "", traceId);
        }
        if (message == null) {
            message = "工具调用成功";
        }
        if ("成功".equals(message)) {
            message = "工具调用成功";
        }
        return new HttpResult(true, false, message, data, traceId);
    }

    public static HttpResult fail(String message) {
        return new HttpResult(false, false, message, null, "");
    }

    public static HttpResult exception(String message, Throwable e) {
        return new HttpResult(false, true, message + ":" + e.getMessage(), null, "");
    }

    public static HttpResult fail(String message, String traceId) {
        return new HttpResult(false, false, message, null, traceId);
    }

    public static HttpResult of(KHttpResponse<Object> responseEntity) {
        if (responseEntity == null) {
            return HttpResult.exception("结果为null", new RuntimeException("响应为null"));
        }
        if (responseEntity.success()) {
            return HttpResult.success(responseEntity.message(), JSON.toJSONString(responseEntity.data()), responseEntity.traceId());
        } else {
            return HttpResult.fail(responseEntity.message(), responseEntity.traceId());
        }
    }

    public String toAiResult() {
        return message + "：" + data;
    }
}
