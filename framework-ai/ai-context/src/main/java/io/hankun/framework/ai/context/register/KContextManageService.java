package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.common.context.IContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: KContextManageService
 * @createAt: 2025/12/25 14:51
 * @author: hankun
 */
public class KContextManageService {

    private final Map<Class<?>, ContextBuilder> builderMap = new HashMap<>();


    public void build(Class<? extends IContext> context) {
        KContextRegister kContextRegister = context.getAnnotation(KContextRegister.class);
        if (kContextRegister == null) {
            return;
        }
        ContextBuilder builder = builderMap.get(kContextRegister.builder());


    }
}
