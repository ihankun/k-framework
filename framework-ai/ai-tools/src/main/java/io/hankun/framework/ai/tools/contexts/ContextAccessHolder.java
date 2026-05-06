package io.hankun.framework.ai.tools.contexts;

import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.commons.context.KContextHolder;

/**
 * @description:
 * @className: ContextAccessHolder
 * @createAt: 2025/10/23 20:11
 * @author: hankun
 */
public class ContextAccessHolder {

    public static final String CONTEXT_ACCESS = "contextAccess";

    public static ContextAccess get() {
        return KContextHolder.getData(CONTEXT_ACCESS);
    }

    public static void set(ContextAccess contextAccess) {
        KContextHolder.setData(CONTEXT_ACCESS, contextAccess);
    }

    public static void clear() {
        KContextHolder.removeData(CONTEXT_ACCESS);
    }
}
