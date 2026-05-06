package io.hankun.framework.ai.agent.graph;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.context.TaskDataContext;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.NodeUtil;
import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.common.context.KContextManager;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.TaskRecord;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.context.KToolContextHolder;
import io.hankun.framework.ai.store.history.context.TaskRecordContext;
import io.hankun.framework.ai.tools.contexts.ContextAccessHolder;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: GraphExecService
 * @createAt: 2025/10/22 19:44
 * @author: hankun
 */
@Slf4j
@Component
public class GraphExecService {

    private final StatusManageService statusManageService;

    private final KContextManager kContextManager;

    public GraphExecService(StatusManageService statusManageService,
                            KContextManager kContextManager) {
        this.statusManageService = statusManageService;
        this.kContextManager = kContextManager;
    }


    public OverAllState exec(CurrentId currentId, ContextAccess contextAccess, InputParams inputParams,
                             KAgent kAgent) {
        String contextKey = KContextManager.buildKey(currentId);
        try {
            ContextAccessHolder.set(contextAccess);
            KToolContext kToolContext = KToolContextHolder.get();
            kToolContext.setContextKey(contextKey);
            KContext kContext = KContextHolder.get();
            kContextManager.put(contextKey, kContext);
            return execInner(currentId, contextAccess, inputParams, kAgent);
        } finally {
            kContextManager.remove(contextKey);
            ContextAccessHolder.clear();
        }
    }

    @Nullable
    private OverAllState execInner(CurrentId currentId, ContextAccess contextAccess, InputParams inputParams, KAgent kAgent) {
        TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
        OverAllState overAllState;
        NodeOutput nodeOutput = contextAccess.getData(NodeOutput.class);
        TaskExecConfig taskExecConfig = TaskExecConfig.of(inputParams);
        //任务已存在，且为中断状态
        if (!statusManageService.taskFinish(currentId.sessionId(), currentId.agentId())) {
            if (taskDataContext == null) {
                throw new RuntimeException("任务上下文丢失");
            }
            if (!taskDataContext.isInterrupt()) {
                throw new RuntimeException("任务上下文异常");
            }
            OverAllState.HumanFeedback humanFeedback = new OverAllState.HumanFeedback(inputParams.data(),
                    taskDataContext.getInterruptReturnNode());
            taskExecConfig.setContinueTask(true);
            inputParams.data().put(NodeUtil.CURRENT_ID, currentId);
            log.info("任务恢复,currentId={},inputParams={}", currentId, inputParams);
            //恢复任务
            overAllState = resume(currentId, contextAccess, nodeOutput,
                    inputParams, humanFeedback, kAgent.getAndCompile());
        } else {
            //新任务，生成taskId
            taskExecConfig.setContinueTask(false);
            inputParams.data().put(NodeUtil.CURRENT_ID, currentId);
            log.info("任务开始,currentId={},inputParams={}", currentId, inputParams);
            overAllState = call(currentId, contextAccess, nodeOutput,
                    inputParams, kAgent.getAndCompile());
        }
        return overAllState;
    }

    private void onFinish(CurrentId currentId, ContextAccess contextAccess,
                          NodeOutput nodeOutput, InputParams inputParams) {
        DataWithMeta collectResult = nodeOutput.fetchAgentResult(currentId.agentId());
        TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
        TaskExecStatus taskExecStatus;
        if (taskDataContext.isInterrupt()) {
            taskExecStatus = TaskExecStatus.INPUT_REQUIRED;
        } else {
            taskExecStatus = TaskExecStatus.COMPLETED;
        }
        updateResult(currentId, inputParams, contextAccess, collectResult, taskExecStatus);
        log.info("任务结束,currentId={},result={},status={}", currentId, collectResult, taskExecStatus);
    }


    private void onFailed(CurrentId currentId, ContextAccess contextAccess,
                          NodeOutput nodeOutput, InputParams inputParams, Throwable throwable) {
        DataWithMeta collectResult = nodeOutput.fetchAgentResult(currentId.agentId());
        TaskExecStatus status = TaskExecStatus.FAILED;
        updateResult(currentId, inputParams, contextAccess, collectResult, status);
    }

    private void updateResult(CurrentId currentId, InputParams inputParams,
                              ContextAccess contextAccess, DataWithMeta collectResult, TaskExecStatus taskExecStatus) {
        TaskRecordContext taskRecordContext = contextAccess.getDataOrNull(TaskRecordContext.class);
        if (taskRecordContext != null) {
            TaskRecord taskRecord = taskRecordContext.load(currentId.agentId());
            if (taskRecord != null) {
                taskRecord.finish(collectResult.data(), collectResult.meta(), taskExecStatus);
            }
        }
        statusManageService.updateTask(currentId, contextAccess, taskExecStatus, inputParams);
    }


    private OverAllState call(CurrentId currentId,
                              ContextAccess contextAccess, NodeOutput nodeOutput,
                              InputParams inputParams, CompiledGraph compiledGraph) {
        try {
            RunnableConfig runnableConfig = statusManageService.buildRunnableConfig(currentId);
            statusManageService.updateTask(currentId, contextAccess, TaskExecStatus.SUBMITTED, inputParams);
            statusManageService.updateTask(currentId, contextAccess, TaskExecStatus.WORKING, inputParams);
            OverAllState state = compiledGraph.call(inputParams.data(), runnableConfig).orElse(null);
            onFinish(currentId, contextAccess, nodeOutput, inputParams);
            return state;
        } catch (Exception e) {
            log.error("任务执行异常,e=", e);
            onFailed(currentId, contextAccess, nodeOutput, inputParams, e);
            throw e;
        }
    }

    private OverAllState resume(CurrentId currentId,
                                ContextAccess contextAccess, NodeOutput nodeOutput, InputParams inputParams,
                                OverAllState.HumanFeedback humanFeedback, CompiledGraph compiledGraph) {
        try {
            RunnableConfig runnableConfig = statusManageService.buildRunnableConfig(currentId);
            statusManageService.updateTask(currentId, contextAccess, TaskExecStatus.WORKING, inputParams);
            OverAllState state = compiledGraph.resume(humanFeedback, runnableConfig).orElse(null);
            onFinish(currentId, contextAccess, nodeOutput, inputParams);
            return state;
        } catch (Exception e) {
            log.error("任务继续执行异常,e=", e);
            onFailed(currentId, contextAccess, nodeOutput, inputParams, e);
            return null;
        }
    }


}
