package io.hankun.framework.ai.agent.def;

import io.hankun.framework.ai.agent.def.entity.AgentParams;
import io.hankun.framework.ai.agent.entity.DataWithMeta;

import java.util.function.Function;

/**
 * @description:
 * @className: TaskExecutor
 * @createAt: 2025/12/25 11:34
 * @author: hankun
 */
public class TaskExecutor {


    public static class Builder {


        public Builder addNode(String nodeId) {
            return this;
        }

        public Builder addResultMap(String nodeId,
                                    Function<DataWithMeta, AgentParams> mapper) {
            return this;
        }

        public Builder route(String nodeId, Function<DataWithMeta, String> route) {
            return this;
        }


    }
}
