package io.hankun.framework.ai.agent.node.config;

import io.hankun.framework.commons.context.KContextHolder;

/**
 * @description:
 * @className: ActionConfigHolder
 * @createAt: 2025/12/8 16:20
 * @author: hankun
 */
public class ActionConfigHolder {

    public static final String ACTION_CONFIG = "actionConfig";


    public static ActionConfig get() {
        return KContextHolder.getData(ACTION_CONFIG);
    }


    public static void set(ActionConfig actionConfig) {
        KContextHolder.setData(ACTION_CONFIG, actionConfig);
    }

    public static void remove() {
        KContextHolder.removeData(ACTION_CONFIG);
    }
}
