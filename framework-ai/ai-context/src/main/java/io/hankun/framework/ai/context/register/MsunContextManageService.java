package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.common.context.IContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: MsunContextManageService
 * @createAt: 2025/12/25 14:51
 * @author: hankun
 */
public class MsunContextManageService {

    private final Map<Class<?>, ContextBuilder> builderMap = new HashMap<>();


    public void build(Class<? extends IContext> context) {
        MsunContextRegister msunContextRegister = context.getAnnotation(MsunContextRegister.class);
        if (msunContextRegister == null) {
            return;
        }
        ContextBuilder builder = builderMap.get(msunContextRegister.builder());


    }
}
