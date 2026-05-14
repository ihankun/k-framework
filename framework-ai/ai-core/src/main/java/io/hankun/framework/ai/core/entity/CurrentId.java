package io.hankun.framework.ai.core.entity;

/**
 * @description:
 * @className: CurrentId
 * @createAt: 2025/10/16 14:17
 * @author: hankun
 */
public record CurrentId(
        /*
         * 会话id
         */
        String sessionId,
        /*
         * 任务id
         */
        String taskId,
        /*
         * 智能体id
         */
        String agentId,
        /*
         * 用户输入消息的id
         */
        String messageId,
        /*
         * 节点id
         */
        String nodeId,
        /*
         * 子节点id
         */
        String subId,
        /*
         * 上一个节点id
         */
        String beforeNodeId) {

    public CurrentId updateTask(String taskId) {
        return new CurrentId(sessionId, taskId, agentId, messageId, nodeId, subId, beforeNodeId);
    }

    public CurrentId updateNode(String nodeId) {
        return new CurrentId(sessionId, taskId, agentId, messageId, nodeId, "", this.nodeId);
    }

    public CurrentId updateAgentId(String agentId) {
        return new CurrentId(sessionId, taskId, agentId, messageId, nodeId, "", beforeNodeId);
    }

    public CurrentId creteSub(String subId) {
        return new CurrentId(sessionId, taskId, agentId, messageId, nodeId, subId, beforeNodeId);
    }

    public CurrentId withNewMessageId(String messageId) {
        return new CurrentId(sessionId, taskId, agentId, messageId, nodeId, subId, beforeNodeId);
    }

    public CurrentId whenEnd() {
        return new CurrentId(sessionId, taskId, agentId, messageId, "end", "", "");
    }


    public static CurrentId ofMessage(String sessionId, String agentId, String messageId) {
        return new CurrentId(sessionId, "",
                agentId, messageId,
                "", "", "");
    }

    public static CurrentId ofContext(String sessionId, String agentId) {
        return new CurrentId(sessionId, "",
                agentId, "",
                "context-load", "", "");
    }
}
