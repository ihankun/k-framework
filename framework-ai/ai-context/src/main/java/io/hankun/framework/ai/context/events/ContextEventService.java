package io.hankun.framework.ai.context.events;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.session.OperateStatus;
import io.hankun.framework.ai.core.session.SessionStatus;
import io.hankun.framework.ai.core.session.node.TaskNodeStatus;
import io.hankun.framework.ai.core.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @description:
 * @className: ContextEventService
 * @createAt: 2025/11/3 15:29
 * @author: hankun
 */
@Slf4j
@Component
public class ContextEventService {

    private final List<SessionEventListener> sessionEventListeners;

    private final List<OperateEventListener> operateEventListeners;

    private final List<TaskEventListener> taskEventListeners;

    private final List<TaskNodeEventListener> taskNodeEventListeners;

    public ContextEventService(List<SessionEventListener> sessionEventListeners,
                               List<OperateEventListener> operateEventListeners,
                               List<TaskEventListener> taskEventListeners,
                               List<TaskNodeEventListener> taskNodeEventListeners) {
        this.sessionEventListeners = new ArrayList<>(sessionEventListeners);
        this.sessionEventListeners.sort(Comparator.comparingInt(SessionEventListener::getOrder));
        this.operateEventListeners = new ArrayList<>(operateEventListeners);
        this.operateEventListeners.sort(Comparator.comparingInt(OperateEventListener::getOrder));
        this.taskEventListeners = new ArrayList<>(taskEventListeners);
        this.taskEventListeners.sort(Comparator.comparingInt(TaskEventListener::getOrder));
        this.taskNodeEventListeners = new ArrayList<>(taskNodeEventListeners);
        this.taskNodeEventListeners.sort(Comparator.comparingInt(TaskNodeEventListener::getOrder));
    }

    public void onSessionUpdate(SessionStatus sessionStatus, CurrentId currentId,
                                ContextAccess contextAccess, InputParams inputParams) {
        log.debug("session update, currentId: {}, sessionStatus: {}",
                currentId, sessionStatus);
        for (SessionEventListener sessionEventListener : sessionEventListeners) {
            try {
                log.debug("session update, sessionEventListener: {}", sessionEventListener);
                sessionEventListener.onSessionUpdate(sessionStatus, currentId, contextAccess, inputParams);
            } catch (Exception e) {
                log.error("session update error, currentId: {}, sessionStatus: {}, inputParams: {}",
                        currentId, sessionStatus, inputParams, e);
            }
        }
    }

    public void onOperateUpdate(OperateStatus operateStatus, CurrentId currentId,
                                ContextAccess contextAccess, InputParams inputParams) {
        log.debug("operate update, currentId: {}, operateStatus: {}",
                currentId, operateStatus);
        for (OperateEventListener operateEventListener : operateEventListeners) {
            try {
                log.debug("operate update, operateEventListener: {}", operateEventListener);
                operateEventListener.onOperateUpdate(operateStatus, currentId, contextAccess, inputParams);
            } catch (Exception e) {
                log.error("operate update error, currentId: {}, operateStatus: {}, inputParams: {}",
                        currentId, operateStatus, inputParams, e);
            }
        }
    }

    public void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId,
                             ContextAccess contextAccess, InputParams inputParams) {
        log.debug("task update, currentId: {}, taskExecStatus: {}",
                currentId, taskExecStatus);
        for (TaskEventListener taskEventListener : taskEventListeners) {
            try {
                log.debug("task update, taskEventListener: {}", taskEventListener);
                taskEventListener.onTaskUpdate(taskExecStatus, currentId, contextAccess, inputParams);
            } catch (Exception e) {
                log.error("task update error, currentId: {}, taskExecStatus: {}, inputParams: {}",
                        currentId, taskExecStatus, inputParams, e);
            }
        }
    }

    public void onTaskNodeUpdate(TaskNodeStatus taskNodeStatus, CurrentId currentId,
                                 ContextAccess contextAccess, InputParams inputParams) {
        log.debug("task node update, currentId: {}, taskNodeStatus: {}",
                currentId, taskNodeStatus);
        for (TaskNodeEventListener taskNodeEventListener : taskNodeEventListeners) {
            try {
                log.debug("task node update, taskNodeEventListener: {}", taskNodeEventListener);
                taskNodeEventListener.onTaskNodeUpdate(taskNodeStatus, currentId, contextAccess, inputParams);
            } catch (Exception e) {
                log.error("task node update error, currentId: {}, taskNodeStatus: {}, inputParams: {}",
                        currentId, taskNodeStatus, inputParams, e);
            }
        }
    }
}
