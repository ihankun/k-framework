package io.hankun.framework.ai.context.entity;

import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.session.task.MapConfig;
import io.hankun.framework.commons.utils.ContextUtil;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: InputParams
 * @createAt: 2025/10/23 10:00
 * @author: hankun
 */
public record InputParams(String formatInput,
                          Map<String, Object> data,
                          MapConfig config) implements IContext {

    public <T> T get(String key, Class<T> clazz) {
        return ContextUtil.read(data, key, clazz, false);
    }

    public CallerInfo getCallerInfo() {
        return config.get(CallerInfo.class);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        return ContextUtil.readList(data, key, clazz, false);
    }


    public InputParams withData(Map<String, Object> data) {
        return new InputParams(formatInput, data, config);
    }

    public InputParams withFormatInput(String formatInput) {
        return new InputParams(formatInput, data, config);
    }

    public InputParams withInputAndData(String formatInput, Map<String, Object> data) {
        return new InputParams(formatInput, data, config);
    }
}
