package io.hankun.framework.ai.context.entity;


import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.context.store.ContextStoreType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ContextAccess
 * @createAt: 2025/10/16 11:19
 * @author: hankun
 */
public record ContextAccess(ContextData contextData, List<Class<? extends IContext>> keys,
                            Map<Class<? extends IContext>, ContextRegister<? extends IContext>> keyMap) {

    public <T extends IContext> T getData(Class<T> clazz) {
        String key = getKey(clazz);
        return contextData.getData(key, clazz);
    }

    public <T extends IContext> T getDataOrNull(Class<T> clazz) {
        ContextRegister<?> register = keyMap.get(clazz);
        if (register == null) {
            return null;
        }
        return contextData.getData(register.getKey(), clazz);
    }

    @NotNull
    private <T extends IContext> String getKey(Class<T> clazz) {
        ContextRegister<?> register = keyMap.get(clazz);
        if (register == null) {
            throw new IllegalArgumentException("上下文未注册:" + clazz);
        }
        return register.getKey();
    }


    public <T extends IContext> void setData(T value) {
        String key = getKey(value.getClass());
        contextData.setData(key, value);
    }

    public <T extends IContext> void removeData(Class<T> clazz) {
        String key = getKey(clazz);
        contextData.removeData(key);
    }

    public Map<String, Object> loadData(@NotNull ContextStoreType storeType) {
        Map<String, Object> result = new HashMap<>();
        for (ContextRegister<?> register : keyMap.values()) {
            if (register.lifecycle().getStoreType().equals(storeType)) {
                result.put(register.getKey(), contextData.getData(register.getKey(), register.dataType()));
            }
        }
        return result;
    }

}
