package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;

/**
 * @description:
 * @className: ParamsQueueReader
 * @createAt: 2025/12/16 11:05
 * @author: hankun
 */
@FunctionalInterface
public interface ParamsQueueReader {

    @NotNull
    AgentCallParams read(@NotNull BlockingQueue<AgentCallParams> paramsQueue) throws InterruptedException;
}
