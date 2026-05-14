package io.hankun.framework.ai.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.hankun.framework.ai.common.entity.HttpResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @description:
 * @className: KHttpClient
 * @createAt: 2025/6/4 16:35
 * @author: hankun
 */
@Slf4j
public class KHttpClient {

    private static OkHttpClient okHttpClient(HttpConfig httpConfig) {
        ConnectionPool connectionPool = new ConnectionPool(httpConfig.getPoolMaxIdleConnections(),
                httpConfig.getPoolKeepAliveDuration(), TimeUnit.SECONDS);
        return new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(httpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(httpConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    private final OkHttpClient okHttpClient;

    public KHttpClient(HttpConfig httpConfig) {
        okHttpClient = okHttpClient(httpConfig);
    }

    public Request post(String url, String params, Consumer<Request.Builder> headers) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(params, MediaType.parse("application/json")));
        headers.accept(requestBuilder);
        return requestBuilder.build();
    }

    public Request get(String url, Consumer<Request.Builder> headers) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get();
        headers.accept(requestBuilder);
        return requestBuilder.build();
    }

    public HttpResult call(Request request, boolean withResponseResult) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            return getResult(response, withResponseResult);
        } catch (Exception e) {
            log.error("请求出现异常: e=", e);
            return HttpResult.exception("请求出现异常", e);
        }
    }

    private HttpResult getResult(Response response, boolean withResponseResult) {
        if (!response.isSuccessful()) {
            log.error("请求失败: code={},message={}", response.code(), response.message());
            return HttpResult.fail("请求不成功: " + response.message());
        }
        try {
            ResponseBody body = response.body();
            if (body == null) {
                log.error("请求返回空");
                return HttpResult.fail("请求返回空");
            }
            String bodyString = body.string();
            if (!withResponseResult) {
                return HttpResult.success(bodyString);
            }
            JSONObject jsonObject = JSON.parseObject(bodyString);
            Boolean successFlag = jsonObject.getBoolean("success");
            if (successFlag == null) {
                return HttpResult.success(bodyString);
            }
            if (!successFlag) {
                log.error("请求结果失败: body={}", bodyString);
                return HttpResult.fail("失败: " + jsonObject.getString("message"), jsonObject.getString("traceId"));
            }
            return HttpResult.success(jsonObject.getString("message"), jsonObject.getString("data"), jsonObject.getString("traceId"));
        } catch (IOException e) {
            log.error("获取结果失败: e=", e);
            return HttpResult.exception("获取结果失败", e);
        }
    }

}
