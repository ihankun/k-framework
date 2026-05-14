package io.hankun.framework.ai.mem.mem0;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.core.entity.HttpResult;
import io.hankun.framework.ai.core.http.HttpConfig;
import io.hankun.framework.ai.core.http.KHttpClient;
import io.hankun.framework.ai.mem.mem0.entity.MemCreateDto;
import io.hankun.framework.ai.mem.mem0.entity.MemSearchDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

/**
 * @description:
 * @className: AiMem0Service
 * @createAt: 2025/12/3 11:51
 * @author: hankun
 */
@Slf4j
public class AiMem0Service {

    private final KHttpClient kHttpClient;

    private final String url;

    public AiMem0Service(String url, HttpConfig httpConfig) {
        this.url = url;
        this.kHttpClient = new KHttpClient(httpConfig);
    }

    private String buildUrl(String url) {
        return url + url;
    }

    public void add(MemCreateDto dto) {
        Request request = kHttpClient.post(buildUrl("/memories"), JSON.toJSONString(dto), builder -> {
        });
        HttpResult call = kHttpClient.call(request, false);
        if (!call.success()) {
            throw new RuntimeException(call.message());
        }
        log.info("保存成功: {}", call.data());
    }

    public void search(MemSearchDto dto) {
        Request request = kHttpClient.post(buildUrl("/search"), JSON.toJSONString(dto), builder -> {
        });
        HttpResult call = kHttpClient.call(request, false);
        if (!call.success()) {
            throw new RuntimeException(call.message());
        }
        log.info("搜索成功: {}", call.data());
    }
}
