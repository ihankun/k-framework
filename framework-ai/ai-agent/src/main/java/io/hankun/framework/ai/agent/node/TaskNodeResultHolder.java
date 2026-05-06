package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.common.record.TaskNodeRecord;
import io.hankun.framework.commons.context.KContextHolder;

/**
 * @description:
 * @className: TaskNodeResultHolder
 * @createAt: 2025/10/27 14:42
 * @author: hankun
 */
public class TaskNodeResultHolder {

    public static final String NODE_INFO = "nodeInfo";

    public static TaskNodeRecord get() {
        return KContextHolder.getData(NODE_INFO);
    }

    public static void set(TaskNodeRecord taskNodeRecord) {
        KContextHolder.setData(NODE_INFO, taskNodeRecord);
    }

    public static void remove() {
        KContextHolder.removeData(NODE_INFO);
    }
}
