package io.hankun.framework.ai.agent.node.entity;

import com.alibaba.cloud.ai.graph.OverAllState;
import io.hankun.framework.ai.agent.context.TaskDataContext;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.NodeUtil;
import io.hankun.framework.ai.agent.node.TaskNodeResultHolder;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfigHolder;
import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.core.context.CurrentIdHolder;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.record.TaskNodeRecord;
import io.hankun.framework.ai.core.record.TaskRecord;
import io.hankun.framework.ai.core.session.node.TaskNodeStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.store.history.context.TaskRecordContext;
import io.hankun.framework.ai.tools.contexts.ContextAccessHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @description:
 * @className: KNodeContext
 * @createAt: 2025/10/28 08:33
 * @author: hankun
 */
@Slf4j
public record KNodeContext(CurrentId currentId, ContextAccess contextAccess,
                           InputParams inputParams, NodeOutput nodeOutput,
                           ActionConfig actionConfig,
                           StatusManageService statusManageService) {

    public static KNodeContext build(OverAllState state, ActionConfig actionConfig, StatusManageService statusManageService) {
        log.debug("KNode init nodeId:{}，config={}", actionConfig.getNodeId(), actionConfig);
        CurrentId currentId = NodeUtil.getCurrentId(state);
        String input = NodeUtil.getInput(state);
        currentId = currentId.updateNode(actionConfig.getNodeId());
        input = buildInput(actionConfig, input, state.data());
        ContextAccess contextAccess = ContextAccessHolder.get();
        InputParams beforeInput = contextAccess.getData(InputParams.class);
        InputParams inputParams = beforeInput.withInputAndData(input, state.data());
        contextAccess.setData(inputParams);
        NodeOutput nodeOutput = contextAccess.getData(NodeOutput.class);
        CurrentIdHolder.setCurrentId(currentId);
        ActionConfigHolder.set(actionConfig);
        return new KNodeContext(currentId, contextAccess, inputParams, nodeOutput, actionConfig, statusManageService);
    }

    private void start() {
        statusManageService.updateTaskNode(currentId, contextAccess,
                TaskNodeStatus.STARTED, inputParams);
        TaskRecordContext taskRecordContext = contextAccess.getDataOrNull(TaskRecordContext.class);
        if (taskRecordContext != null) {
            TaskNodeRecord taskNodeRecord = TaskNodeRecord.init(currentId, inputParams.formatInput());
            TaskNodeResultHolder.set(taskNodeRecord);
            TaskRecord load = taskRecordContext.load(currentId.agentId());
            if (load != null) {
                load.getTaskNodeRecords().add(taskNodeRecord);
            }
        }
    }

    public void startNode() {
        log.info("节点【{}】({}) 开始执行", actionConfig.getNodeDesc(), actionConfig.getNodeId());
        start();
    }

    public void startEdge() {
        log.info("条件边节点【{}】({}) 开始执行", actionConfig.getNodeDesc(), actionConfig.getNodeId());
        start();
    }

    private static String buildInput(ActionConfig actionConfig, String input, Map<String, Object> inputParams) {
        String inputFormat = actionConfig.getInputFormat();
        if (ObjectUtils.isEmpty(inputFormat)) {
            return input;
        } else {
            return PromptTemplate.builder().
                    template(inputFormat).variables(inputParams)
                    .renderer(StTemplateRenderer.builder().build())
                    .build().render();
        }
    }

    public void finishNode(KNodeResult nodeResult) {
        finish(nodeResult);
        log.info("节点【{}】({}) 结束执行", actionConfig.getNodeDesc(), actionConfig.getNodeId());
    }

    public void finishEdge(String result) {
        finish(KNodeResult.ofText(actionConfig.getNodeId(), result));
        log.info("条件边节点【{}】({}) 结束执行", actionConfig.getNodeDesc(), actionConfig.getNodeId());
    }

    private void finish(KNodeResult data) {
        TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
        taskDataContext.setBeforeId(currentId);
        taskDataContext.setBeforeResult(data);
        TaskNodeRecord taskNodeRecord = TaskNodeResultHolder.get();
        if (taskNodeRecord != null) {
            taskNodeRecord.finish(data.buildNodeOutputJson());
        }
        statusManageService.updateTaskNode(currentId, contextAccess,
                TaskNodeStatus.COMPLETED, inputParams);

    }

    public void failNode(Throwable throwable) {
        log.error("节点【{}】({}) 执行失败,config={},e=", actionConfig.getNodeDesc(), actionConfig.getNodeId(), actionConfig, throwable);
        statusManageService.updateTaskNode(currentId, contextAccess,
                TaskNodeStatus.FAILED, inputParams);
    }

    public void close() {
        TaskNodeResultHolder.remove();
        ActionConfigHolder.remove();
        CurrentIdHolder.removeCurrentId();
    }

    public void failEdge(Throwable throwable) {
        log.error("条件边节点【{}】({}) 执行失败,config={},e=", actionConfig.getNodeDesc(), actionConfig.getNodeId(), actionConfig, throwable);
        statusManageService.updateTaskNode(currentId, contextAccess,
                TaskNodeStatus.FAILED, inputParams);
    }
}
