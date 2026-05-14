package io.hankun.framework.ai.mcp.app;

import com.alibaba.fastjson.JSON;
import io.hankun.framework.ai.core.context.CurrentIdHolder;
import io.hankun.framework.ai.core.entity.HttpResult;
import io.hankun.framework.ai.mcp.app.caller.IKHttpCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @className: KHttpService
 * @createAt: 2025/5/28 17:11
 * @author: hankun
 */
@Slf4j
@Component
public class KHttpService {

    private final IKHttpCaller kHttpCaller;

    public KHttpService(IKHttpCaller kHttpCaller) {
        this.kHttpCaller = kHttpCaller;
    }


    public HttpResult call(String httpMethod, String path, String params) {
        if ("GET".equalsIgnoreCase(httpMethod)) {
            Map<String, Object> paramMap = JSON.parseObject(params);
            //获取会话id
            String conversionId = CurrentIdHolder.getCurrentId().sessionId();
            paramMap.put("conversionId", conversionId);
            return get(path, paramMap);
        } else if ("POST".equalsIgnoreCase(httpMethod)) {
            return post(path, params);
        }
        return HttpResult.fail("不支持" + httpMethod + "接口类型");
    }

    public HttpResult get(String path, Map<String, Object> params) {
        return kHttpCaller.get(path, params);
    }

    public HttpResult post(String path, String requestBody) {
        return kHttpCaller.post(path, requestBody);
    }


}
