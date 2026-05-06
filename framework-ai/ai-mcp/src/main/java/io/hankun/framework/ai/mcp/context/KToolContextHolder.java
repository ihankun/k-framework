package io.hankun.framework.ai.mcp.context;


import io.hankun.framework.commons.context.KContextHolder;

/**
 * @description:
 * @className: MsunToolContextHolder
 * @createAt: 2025/6/5 14:51
 * @author: hankun
 */
public class KToolContextHolder {

    public static final String K_CONTEXT = "k-context";


    public static KToolContext get() {
        return KContextHolder.getData(K_CONTEXT);
    }


    public static void set(KToolContext context) {
        KContextHolder.setData(K_CONTEXT, context);
    }


    public static void clear() {
        KContextHolder.removeData(K_CONTEXT);
    }
}
