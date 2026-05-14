package io.hankun.framework.ai.agent.config;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.exec.AgentGraph;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.config.ReplyConfig;
import io.hankun.framework.ai.core.session.task.MapConfig;
import io.hankun.framework.ai.context.entity.CallerInfo;
import io.hankun.framework.ai.context.entity.InputParams;

import java.util.Map;

/**
 * @description:
 * @className: TaskExecConfig
 * @createAt: 2025/12/8 09:45
 * @author: hankun
 */
public record TaskExecConfig(MapConfig config) {

    public AgentGraph getAgentGraph() {
        return config.get(AgentGraph.class);
    }

    public void setAgentGraph(AgentGraph agentGraph) {
        config.set(agentGraph);
    }

    public AgentConfig getCurrentAgentConfig() {
        return config.get(AgentConfig.class);
    }


    public ActionConfig getNodeConfig(String nodeId) {
        AgentConfig agentConfig = getCurrentAgentConfig();
        AgentGraph agentGraph = getAgentGraph();
        KAgent agent = agentGraph.getAgent(agentConfig.getAgentCode());
        return agent.getConfigMap().get(nodeId);
    }

    public void setCurrentAgentConfig(AgentConfig agentConfig) {
        config.set(agentConfig);
    }

    public boolean continueTask() {
        Boolean continueTask = config.get("continueTask", Boolean.class);
        return continueTask != null && continueTask;
    }

    public void setContinueTask(boolean continueTask) {
        config.set("continueTask", continueTask);
    }


    public CallerInfo getCallerInfo() {
        return config.get(CallerInfo.class);
    }

    public void setCallerInfo(CallerInfo callerInfo) {
        config.set(callerInfo);
    }

    public ReplyConfig getReplay() {
        ReplyConfig replay = config.get("replay", ReplyConfig.class);
        return replay != null ? replay : ReplyConfig.def();
    }

    public void setReplay(ReplyConfig replay) {
        config.set("replay", replay);
    }

    public Boolean getClearWhenFailed() {
        Boolean clearWhenFailed = config.get("clearWhenFailed", Boolean.class);
        if (clearWhenFailed == null) {
            return true;
        }
        return clearWhenFailed;
    }

    public void setClearWhenFailed(Boolean clearWhenFailed) {
        config.set("clearWhenFailed", clearWhenFailed);
    }

    public static TaskExecConfig of() {
        return new TaskExecConfig(MapConfig.of());
    }

    public static TaskExecConfig of(InputParams inputParams) {
        return new TaskExecConfig(inputParams.config());
    }

    public static TaskExecConfig of(Map<String, Object> config) {
        return new TaskExecConfig(new MapConfig(config));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ReplyConfig replyConfig = ReplyConfig.def();

        private boolean clearWhenFailed = true;

        public Builder reply(ReplyConfig replyConfig) {
            this.replyConfig = replyConfig;
            return this;
        }

        public Builder clearWhenFailed(boolean clearWhenFailed) {
            this.clearWhenFailed = clearWhenFailed;
            return this;
        }

        public TaskExecConfig build() {
            TaskExecConfig taskExecConfig = TaskExecConfig.of();
            taskExecConfig.setReplay(replyConfig);
            taskExecConfig.setClearWhenFailed(clearWhenFailed);
            return taskExecConfig;
        }
    }
}
