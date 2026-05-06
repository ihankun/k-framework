package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.common.entity.CurrentId;
import lombok.Data;

/**
 * @description:
 * @className: TaskDataContext
 * @createAt: 2025/10/20 15:21
 * @author: hankun
 */
@Data
public class TaskDataContext implements IContext {

    private volatile CurrentId beforeId;

    private volatile KNodeResult beforeResult;

    private volatile String beforeTarget;

    private volatile boolean beforeOutput = false;

    private volatile boolean interrupt = false;

    private volatile String interruptReturnNode;

    public static TaskDataContext build() {
        TaskDataContext context = new TaskDataContext();
        context.setBeforeId(null);
        context.setBeforeResult(null);
        return context;
    }

    public void interrupt(String interruptReturnNode) {
        this.interrupt = true;
        this.interruptReturnNode = interruptReturnNode;
    }

    public void resetInterrupt() {
        this.interrupt = false;
        this.interruptReturnNode = null;
    }


    public DataWithMeta fetchBeforeOutResult() {
        if (beforeResult == null) {
            return null;
        }
        return beforeResult.getResult();
    }
}
