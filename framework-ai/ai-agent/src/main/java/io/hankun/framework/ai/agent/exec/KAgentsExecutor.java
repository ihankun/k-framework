package io.hankun.framework.ai.agent.exec;

import com.alibaba.cloud.ai.graph.OverAllState;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.api.entity.GraphParams;
import io.hankun.framework.ai.agent.build.AgentLoadService;
import io.hankun.framework.ai.agent.call.GlobalOutput;
import io.hankun.framework.ai.agent.call.entity.AgentCallKey;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.ai.agent.exceptions.KAiException;
import io.hankun.framework.ai.agent.graph.GraphExecService;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.NodeUtil;
import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.redis.KRedisHolder;
import io.hankun.framework.ai.core.session.OperateStatus;
import io.hankun.framework.ai.core.session.SessionStatus;
import io.hankun.framework.ai.context.ContextManageService;
import io.hankun.framework.ai.context.entity.CallerInfo;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.store.ContextStoreType;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.context.KToolContextHolder;
import io.hankun.framework.ai.store.history.context.ChatRecordContext;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import io.hankun.framework.commons.utils.ContextUtil;
import io.hankun.framework.core.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @className: KAgentsExecutor
 * @createAt: 2025/10/30 14:01
 * @author: hankun
 */

@Slf4j
public class KAgentsExecutor {

    private final GraphExecService graphExecService;

    private final ContextManageService contextManageService;
    private final List<KAgent> agentList;

    private final ExecutorService executorService;

    private final Set<Class<? extends IContext>> agentContexts;

    private final StatusManageService statusManageService;

    private final AgentLoadService agentLoadService;

    private final AgentGraph agentGraph;

    private final RedissonClient redissonClient;

    private final String lockKey;

    public KAgentsExecutor(GraphExecService graphExecService,
                           ContextManageService contextManageService,
                           List<KAgent> agentList,
                           ExecutorService executorService,
                           StatusManageService statusManageService,
                           AgentLoadService agentLoadService,
                           KRedisHolder kRedisHolder) {
        this.graphExecService = graphExecService;
        this.contextManageService = contextManageService;
        this.agentList = new ArrayList<>(agentList);
        this.executorService = executorService;
        this.statusManageService = statusManageService;
        this.agentLoadService = agentLoadService;
        this.agentContexts = new HashSet<>();
        this.agentGraph = AgentGraph.build(agentList);
        agentList.forEach(agent -> agentContexts.addAll(agent.getAgentConfig().getContexts()));
        this.redissonClient = kRedisHolder.getRedissonClient();
        this.lockKey = kRedisHolder.getKeyPrefix("exec-lock");
    }

    public Flux<NodeResultData> exec(String sessionId,
                                     String input,
                                     Map<String, Object> params,
                                     KToolContext kToolContext) {
        return exec(sessionId, input, params, kToolContext, TaskExecConfig.builder().build());
    }

    public Flux<NodeResultData> exec(String sessionId,
                                     String input,
                                     Map<String, Object> params,
                                     KToolContext kToolContext, TaskExecConfig taskExecConfig) {
        return exec(sessionId, nextMessageId(), input, params, kToolContext, taskExecConfig, null, false)
                .output();
    }

    public AgentCallResponse exec(String sessionId,
                                  String messageId,
                                  String input,
                                  Map<String, Object> params,
                                  KToolContext kToolContext,
                                  TaskExecConfig taskExecConfig,
                                  GlobalOutput globalOutput, boolean writeToGlobal) {
        String taskId = statusManageService.loadTaskId(sessionId);
        AgentCallKey agentCallKey = new AgentCallKey(sessionId, taskId, messageId);
        NodeOutput nodeOutput;
        if (globalOutput != null) {
            nodeOutput = NodeOutput.of(agentCallKey, globalOutput);
        } else {
            nodeOutput = NodeOutput.of(agentCallKey);
        }
        taskExecConfig.setAgentGraph(agentGraph);
        taskExecConfig.setCallerInfo(CallerInfo.of(IdGenerator.ins().generator(), Map.of()));
        agentList.replaceAll(agentLoadService::fresh);
        //构建taskId：sessionId、agentId、messageId
        CurrentId currentId = CurrentId.ofMessage(sessionId, "", messageId);
        Map<String, Object> state = new HashMap<>(params);
        state.put(NodeUtil.INPUT, input);
        InputParams inputParams = new InputParams(input, state, taskExecConfig.config());
        if (writeToGlobal) {
            nodeOutput.writeToGlobal();
        }
        execAsync(currentId, inputParams, nodeOutput, kToolContext);
        return nodeOutput.output();
    }

    public OverAllState blockExec(String sessionId,
                                  String messageId,
                                  String input,
                                  Map<String, Object> params,
                                  CallerInfo callerInfo,
                                  TaskExecConfig taskExecConfig,
                                  NodeOutput nodeOutput) {
        String taskId = statusManageService.loadTaskId(sessionId);
        AgentCallKey agentCallKey = new AgentCallKey(sessionId, taskId, messageId);
        taskExecConfig.setAgentGraph(agentGraph);
        taskExecConfig.setCallerInfo(callerInfo);
        agentList.replaceAll(agentLoadService::fresh);
        //构建taskId：sessionId、agentId、messageId
        CurrentId currentId = CurrentId.ofMessage(sessionId, "", messageId);
        Map<String, Object> state = new HashMap<>(params);
        state.put(NodeUtil.INPUT, input);
        InputParams inputParams = new InputParams(input, state, taskExecConfig.config());
        return execInner(currentId, inputParams, nodeOutput, KContextHolder.get());
    }

    public CompletableFuture<OverAllState> execAsync(CurrentId currentId, InputParams inputParams,
                                                     NodeOutput nodeOutput, KToolContext kToolContext) {
        KToolContext old = KToolContextHolder.get();
        try {
            KToolContextHolder.set(kToolContext);
            KContext kContext = KContextHolder.capture();
            return CompletableFuture.supplyAsync(() -> {
                return execInner(currentId, inputParams, nodeOutput, kContext);
            }, executorService);
        } finally {
            KToolContextHolder.set(old);
        }
    }

    @Nullable
    private OverAllState execInner(CurrentId currentId, InputParams inputParams,
                                   NodeOutput nodeOutput,
                                   KContext kContext) {
        KContext old = KContextHolder.get();
        try {
            if (!Objects.equals(old, kContext)) {
                KContextHolder.set(kContext);
            }
            CallerInfo callerInfo = inputParams.getCallerInfo();
            OverAllState overAllState = exec(currentId, inputParams, nodeOutput, callerInfo.threadId());
            nodeOutput.emitComplete();
            log.debug("会话执行成功,overAllState={}", overAllState);
            return overAllState;
        } catch (Exception e) {
            log.error("会话执行失败,currentId={},e=", currentId, e);
            nodeOutput.emitError(e);
            TaskExecConfig taskExecConfig = TaskExecConfig.of(inputParams);
            Boolean clearWhenFailed = taskExecConfig.getClearWhenFailed();
            if (e instanceof KAiException aiException) {
                nodeOutput.emit(DataWithMeta.ofText(
                        aiException.getMessage()));
                if (aiException.isClearMem() && clearWhenFailed) {
                    statusManageService.clearData(currentId.sessionId());
                }
            } else {
                nodeOutput.emit(DataWithMeta.ofText("很抱歉，出现了一些预料之外的问题，让我们重新开始吧"));
                if (clearWhenFailed) {
                    statusManageService.clearData(currentId.sessionId());
                }
            }
            return null;
        } finally {
            KContextHolder.set(old);
        }
    }

    private boolean tryLock(RLock lock, Long threadId) {
        try {
            int maxTime = 0;
            for (KAgent agent : agentList) {
                maxTime += agent.getAgentConfig().getMaxExecTimeSeconds();
            }
            return lock.tryLockAsync(1, maxTime, TimeUnit.SECONDS, threadId).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取锁失败", e);
            return false;
        }
    }

    private void unLock(RLock lock, Long threadId) {
        try {
            lock.unlockAsync(threadId).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("释放锁失败", e);
        }
    }


    private OverAllState exec(CurrentId currentId, InputParams inputParams,
                              NodeOutput nodeOutput, Long threadId) {
        String sessionId = currentId.sessionId();
        Map<String, Object> state = new HashMap<>(inputParams.data());
        OverAllState result = null;
        ContextAccess contextAccess = contextManageService.loadContext(currentId.sessionId()
                , "", agentContexts, null);
        contextAccess.setData(nodeOutput);
        contextAccess.setData(inputParams);
        RLock lock = getLock(sessionId);
        if (tryLock(lock, threadId)) {
            try {
                statusManageService.updateSession(currentId, SessionStatus.OPEN, contextAccess, inputParams);
                for (KAgent kAgent : agentList) {
                    TaskExecConfig taskExecConfig = TaskExecConfig.of(inputParams);
                    taskExecConfig.setCurrentAgentConfig(kAgent.getAgentConfig());
                    currentId = currentId.updateAgentId(kAgent.getCode());
                    for (Map.Entry<String, GraphParams> defParam : kAgent.getGraphParams().entrySet()) {
                        GraphParams param = defParam.getValue();
                        if (param.defValue() != null) {
                            state.putIfAbsent(param.paramKey(), param.defValue());
                        }
                    }
                    //加载context，依赖sessionId、agentId
                    Map<String, Object> bindData = null;
                    bindData = contextAccess.loadData(ContextStoreType.ACROSS_AGENT);
                    contextAccess = contextManageService.loadContext(sessionId, kAgent.getCode(),
                            kAgent.getAgentConfig().getContexts(), bindData);
                    InputParams currentInputParams = inputParams.withData(state);
                    contextAccess.setData(currentInputParams);
                    try {
                        currentId = currentId.updateTask(statusManageService.loadTaskId(currentId.sessionId()));
                        statusManageService.updateOperate(currentId, contextAccess, OperateStatus.START, inputParams);
                        result = graphExecService.exec(currentId, contextAccess, currentInputParams, kAgent);
                        statusManageService.updateOperate(currentId, contextAccess, OperateStatus.COMPLETE, inputParams);
                    } catch (Exception e) {
                        log.error("执行失败,agent={},currentId={},e=", kAgent.getCode(), currentId, e);
                        statusManageService.updateOperate(currentId, contextAccess, OperateStatus.FAILED, inputParams);
                        throw e;
                    }
                    if (result != null) {
                        Boolean breakExec = ContextUtil.getData(result.data(), "breakExec");
                        if (breakExec != null && breakExec) {
                            log.debug("执行中断,currentId={}", currentId);
                            finishSession(currentId, inputParams, contextAccess, SessionStatus.CLOSED, nodeOutput);
                            return result;
                        }
                        state.putAll(result.data());
                    } else {
                        log.error("执行失败,agent={},currentId={}", kAgent.getCode(), currentId);
                        throw new RuntimeException("执行失败，返回空");
                    }
                }
                if (result != null) {
                    log.debug("执行成功,currentId={}", currentId);
                    finishSession(currentId, inputParams, contextAccess, SessionStatus.CLOSED, nodeOutput);
                    return result;
                } else {
                    log.error("执行失败,结果为空,currentId={}", currentId);
                    throw new RuntimeException("执行失败，返回空");
                }
            } catch (RuntimeException e) {
                log.error("执行失败,currentId={}", currentId, e);
                finishSession(currentId, inputParams, contextAccess, SessionStatus.FAILED, nodeOutput);
                throw e;
            } finally {
                unLock(lock, threadId);
            }
        } else {
            log.error("请不要过于频繁调用,currentId={}", currentId);
            throw new KAiException("请不要过于频繁调用");
        }
    }

    private void finishSession(CurrentId currentId, InputParams inputParams,
                               ContextAccess contextAccess, SessionStatus sessionStatus, NodeOutput nodeOutput) {
        if (contextAccess != null) {
            ChatRecordContext chatRecordContext = contextAccess.getDataOrNull(ChatRecordContext.class);
            if (chatRecordContext != null) {
                DataWithMeta collectResult = nodeOutput.fetchAllResult();
                chatRecordContext.currentRecord().finish(collectResult.data(), collectResult.meta());
            }
        }
        statusManageService.updateSession(currentId, sessionStatus, contextAccess, inputParams);
    }

    private RLock getLock(String sessionId) {
        return redissonClient.getLock(lockKey + sessionId);
    }


    public static String nextMessageId() {
        return UUID.randomUUID().toString();
    }
}
