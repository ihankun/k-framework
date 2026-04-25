package io.hankun.framework.dify.client.callback;


import io.hankun.framework.dify.client.event.NodeFinishedEvent;
import io.hankun.framework.dify.client.event.NodeStartedEvent;
import io.hankun.framework.dify.client.event.WorkflowFinishedEvent;
import io.hankun.framework.dify.client.event.WorkflowStartedEvent;

/**
 * 工作流编排对话型应用流式回调接口
 * 继承自 ChatStreamCallback，增加工作流相关回调方法
 */
public interface ChatflowStreamCallback extends ChatStreamCallback {
    /**
     * 工作流开始事件
     *
     * @param event 事件数据
     */
    default void onWorkflowStarted(WorkflowStartedEvent event) {
    }

    /**
     * 节点开始事件
     *
     * @param event 事件数据
     */
    default void onNodeStarted(NodeStartedEvent event) {
    }

    /**
     * 节点完成事件
     *
     * @param event 事件数据
     */
    default void onNodeFinished(NodeFinishedEvent event) {
    }

    /**
     * 工作流完成事件
     *
     * @param event 事件数据
     */
    default void onWorkflowFinished(WorkflowFinishedEvent event) {
    }
}
