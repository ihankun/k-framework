package io.hankun.framework.ai.agent.task;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.checkpoint.BaseCheckpointSaver;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.context.HumanFeedbackContext;
import io.hankun.framework.ai.agent.node.NodeUtil;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.redis.KRedisHolder;
import io.hankun.framework.ai.common.session.OperateStatus;
import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.common.session.node.TaskNodeStatus;
import io.hankun.framework.ai.common.session.task.MapConfig;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.ContextManageService;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.events.ContextEventService;
import io.hankun.framework.ai.context.store.ContextStoreType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @description:
 * @className: StatusManageService
 * @createAt: 2025/10/30 14:18
 * @author: hankun
 */
@Slf4j
@Component
public class StatusManageService {

    private final ContextEventService contextEventService;

    private final ContextManageService contextManageService;

    private final RedissonClient redissonClient;

    private final String sessionPrefixKey;

    public StatusManageService(ContextEventService contextEventService,
                               ContextManageService contextManageService,
                               KRedisHolder kRedisHolder) {
        this.contextEventService = contextEventService;
        this.contextManageService = contextManageService;
        this.sessionPrefixKey = kRedisHolder.getCommKeyPrefix("sessionStatus");
        this.redissonClient = kRedisHolder.getRedissonClient();
    }


    public TaskStatusData getTaskStatus(String sessionId, String agentId) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        return sessionStatusData.getTask(agentId);
    }

    public SessionStatusData getSessionStatus(String sessionId) {
        RBucket<SessionStatusData> sessionStatusRBucket = getSessionBucket(sessionId);
        SessionStatusData sessionStatusData = sessionStatusRBucket.get();
        if (sessionStatusData == null) {
            sessionStatusData = new SessionStatusData();
            sessionStatusData.setStatus(SessionStatus.OPEN);
            sessionStatusData.setTaskId(nextTaskId());
            sessionStatusRBucket.set(sessionStatusData);
            return sessionStatusData;
        }
        return sessionStatusData;
    }

    private RBucket<SessionStatusData> getSessionBucket(String sessionId) {
        return redissonClient.getBucket(sessionPrefixKey + sessionId);
    }


    private void saveStatus(CurrentId currentId, SessionStatusData sessionStatusData) {
        RBucket<SessionStatusData> sessionStatusRBucket = getSessionBucket(currentId.sessionId());
        sessionStatusRBucket.set(sessionStatusData);
        sessionStatusRBucket.expire(Duration.ofHours(24));
    }

    private void clearStatus(String sessionId) {
        RBucket<SessionStatusData> sessionStatusRBucket = getSessionBucket(sessionId);
        sessionStatusRBucket.delete();
    }


    public String loadTaskId(String sessionId) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        return sessionStatusData.getTaskId();
    }

    public String loadHumanRequired(String sessionId, String agentId) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        TaskStatusData taskStatusData = sessionStatusData.loadTask(agentId);
        if (TaskExecStatus.INPUT_REQUIRED.equals(taskStatusData.getTaskExecStatus())) {
            return taskStatusData.getInputRequired();
        } else {
            taskStatusData.setInputRequired("");
            return "";
        }
    }

    public List<String> loadHumanRequired(String sessionId) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        List<String> requiredList = new ArrayList<>();
        for (TaskStatusData taskStatusData : sessionStatusData.showTasks()) {
            if (TaskExecStatus.INPUT_REQUIRED.equals(taskStatusData.getTaskExecStatus())) {
                if (StringUtils.hasText(taskStatusData.getInputRequired())) {
                    requiredList.add(taskStatusData.getInputRequired());
                }
            }
        }
        return requiredList;
    }

    public void updateTask(CurrentId currentId,
                           ContextAccess contextAccess,
                           TaskExecStatus taskExecStatus,
                           InputParams inputParams) {
        SessionStatusData sessionStatusData = getSessionStatus(currentId.sessionId());
        TaskStatusData taskStatusData = sessionStatusData.loadTask(currentId.agentId());
        if (TaskExecStatus.INPUT_REQUIRED.equals(taskExecStatus)) {
            HumanFeedbackContext humanFeedbackContext = contextAccess.getDataOrNull(HumanFeedbackContext.class);
            if (humanFeedbackContext != null) {
                taskStatusData.setInputRequired(humanFeedbackContext.getRequired());
            }
        } else {
            taskStatusData.setInputRequired("");
        }
        taskStatusData.setTaskExecStatus(taskExecStatus);
        contextEventService.onTaskUpdate(taskExecStatus, currentId, contextAccess, inputParams);
        contextManageService.onTaskUpdate(taskExecStatus, currentId, contextAccess, inputParams);
        if (taskExecStatus.isFinal()) {
            sessionStatusData.remove(currentId.agentId());
        }
        taskStatusData.setCurrentId(currentId);
        saveStatus(currentId, sessionStatusData);
    }

    public void updateTaskNode(CurrentId currentId,
                               ContextAccess contextAccess,
                               TaskNodeStatus taskNodeStatus,
                               InputParams inputParams) {
        contextEventService.onTaskNodeUpdate(taskNodeStatus, currentId, contextAccess, inputParams);
        contextManageService.onTaskNodeUpdate(taskNodeStatus, currentId, contextAccess, inputParams);
        if (taskNodeStatus.isFinal()) {
            contextManageService.clearNodeContext(currentId, contextAccess);
        }
        SessionStatusData sessionStatusData = getSessionStatus(currentId.sessionId());
        TaskStatusData taskStatusData = sessionStatusData.loadTask(currentId.agentId());
        taskStatusData.setCurrentId(currentId);
    }

    public void updateOperate(CurrentId currentId,
                              ContextAccess contextAccess,
                              OperateStatus operateStatus,
                              InputParams inputParams) {
        SessionStatusData sessionStatusData = getSessionStatus(currentId.sessionId());
        contextEventService.onOperateUpdate(operateStatus, currentId, contextAccess, inputParams);
        contextManageService.onOperateUpdate(operateStatus, currentId, contextAccess, inputParams);
        if (operateStatus.isFinal()) {
            contextManageService.saveContext(currentId, contextAccess, sessionStatusData.checkFinish(currentId.agentId()));
        }
    }


    public void updateSession(CurrentId currentId, SessionStatus sessionStatus, ContextAccess contextAccess, InputParams inputParams) {
        contextEventService.onSessionUpdate(sessionStatus, currentId, contextAccess, inputParams);
        contextManageService.onSessionUpdate(sessionStatus, currentId, contextAccess, inputParams);
        SessionStatusData sessionStatusData = getSessionStatus(currentId.sessionId());
        sessionStatusData.setStatus(sessionStatus);
        boolean isContinue = false;
        for (TaskStatusData taskStatusData : sessionStatusData.showTasks()) {
            if (!taskStatusData.getTaskExecStatus().isFinal()) {
                isContinue = true;
            }
        }
        if (sessionStatus.isFinal() && !isContinue) {
            clearStatus(currentId.sessionId());
            return;
        }
        saveStatus(currentId, sessionStatusData);
    }

    public void clearData(String sessionId) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        for (TaskStatusData taskStatusData : sessionStatusData.showTasks()) {
            contextManageService.clearContext(sessionId, taskStatusData.getAgentId());
        }
        clearStatus(sessionId);
    }

    public void cancelTask(String sessionId, KAgent kAgent) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        TaskStatusData taskStatusData = sessionStatusData.loadTask(kAgent.getCode());
        if (TaskExecStatus.INPUT_REQUIRED.equals(taskStatusData.getTaskExecStatus())) {
            CurrentId currentId = taskStatusData.getCurrentId();
            if (currentId != null) {
                ContextAccess contextAccess = contextManageService.loadContext(sessionId,
                        kAgent.getCode(), kAgent.getAgentConfig().getContexts(), null);
                BaseCheckpointSaver checkpointSaver = kAgent.getCheckpointSaver();
                checkpointSaver.clear(buildRunnableConfig(currentId));
                InputParams currentInputParams = loadInputParams(currentId);
                updateTask(currentId, contextAccess, TaskExecStatus.CANCELED, currentInputParams);
                updateOperate(currentId, contextAccess, OperateStatus.CANCEL, currentInputParams);
                updateSession(currentId, SessionStatus.CLOSED, contextAccess, currentInputParams);
            } else {
                log.warn("cancel task fail, currentId is null");
            }
        } else {
            log.warn("cancel task fail, task is not interrupt");
        }
        contextManageService.clearContext(sessionId, kAgent.getCode(), ContextStoreType.IN_AGENT);
    }

    @NotNull
    private static InputParams loadInputParams(CurrentId currentId) {
        //TODO caller设置
        return new InputParams("", Map.of(NodeUtil.CURRENT_ID, currentId),
                MapConfig.of());
    }

    public boolean taskInterrupt(String sessionId) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        return sessionStatusData.checkInterrupt();
    }

    public boolean taskFinish(String sessionId, String agentId) {
        SessionStatusData sessionStatusData = getSessionStatus(sessionId);
        return sessionStatusData.checkFinish(agentId);
    }

    public void clearSession(String sessionId, KAgent kAgent) {
        contextManageService.clearAllContext(sessionId, kAgent.getCode());
    }

    private String nextTaskId() {
        return UUID.randomUUID().toString();
    }

    public RunnableConfig buildRunnableConfig(CurrentId currentId) {
        return RunnableConfig.builder()
                .threadId(currentId.sessionId() + ":" + currentId.taskId() + ":" + currentId.agentId())
                .build();
    }
}
