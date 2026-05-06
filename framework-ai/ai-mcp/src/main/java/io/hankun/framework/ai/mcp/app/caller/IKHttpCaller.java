package io.hankun.framework.ai.mcp.app.caller;

import io.hankun.framework.ai.common.entity.HttpResult;

import java.util.Map;

/**
 * @description:
 * @className: IKHttpCaller
 * @createAt: 2026/1/4 11:39
 * @author: hankun
 */
public interface IKHttpCaller {

    HttpResult get(String path, Map<String, Object> params);


    HttpResult post(String path, String requestBody);
}
