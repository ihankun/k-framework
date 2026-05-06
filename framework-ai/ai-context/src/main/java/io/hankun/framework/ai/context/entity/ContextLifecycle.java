package io.hankun.framework.ai.context.entity;

import io.hankun.framework.ai.common.session.OperateStatus;
import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.register.RegisterTime;
import io.hankun.framework.ai.context.store.ContextStoreType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: ContextLifecycle
 * @createAt: 2025/11/4 13:47
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum ContextLifecycle {

    SESSION("session", "会话，同个会话，跨任务可访问", ContextStoreType.ACROSS_AGENT) {
        @Override
        public boolean checkPersistence(boolean taskFinish) {
            return true;
        }

        @Override
        public RegisterTime.Builder defRegisterTime() {
            return RegisterTime.builder()
                    .registerAt(SessionStatus.OPEN);
        }
    },

    TASK("task", "任务，同个任务，跨agent可访问", ContextStoreType.ACROSS_AGENT) {
        @Override
        public boolean checkPersistence(boolean taskFinish) {
            return !taskFinish;
        }

        @Override
        public RegisterTime.Builder defRegisterTime() {
            return RegisterTime.builder()
                    .registerAt(TaskExecStatus.SUBMITTED);
        }
    },

    MESSAGE("message", "消息，同一次请求（操作），跨agent可访问", ContextStoreType.ACROSS_AGENT) {
        @Override
        public boolean checkPersistence(boolean taskFinish) {
            return false;
        }

        @Override
        public RegisterTime.Builder defRegisterTime() {
            return RegisterTime.builder()
                    .registerAt(OperateStatus.START);
        }
    },

    TASK_IN_AGENT("taskInAgent", "任务的某个agent，同一个任务，跨agent不可见", ContextStoreType.IN_AGENT) {
        @Override
        public boolean checkPersistence(boolean taskFinish) {
            return !taskFinish;
        }

        @Override
        public RegisterTime.Builder defRegisterTime() {
            return RegisterTime.builder()
                    .registerAt(TaskExecStatus.SUBMITTED);
        }
    },

    TASK_IN_AGENT_INHERIT("taskInAgentInherit", "任务的某个agent，同一个任务，跨agent不可见，任务结束后依然保留",
            ContextStoreType.IN_AGENT) {
        @Override
        public boolean checkPersistence(boolean taskFinish) {
            return true;
        }

        @Override
        public RegisterTime.Builder defRegisterTime() {
            return RegisterTime.builder()
                    .registerAt(TaskExecStatus.SUBMITTED);
        }
    },

    MESSAGE_IN_AGENT("messageInAgent", "消息的某个agent，同一次请求（操作），跨agent不可见", ContextStoreType.IN_AGENT) {
        @Override
        public boolean checkPersistence(boolean taskFinish) {
            return false;
        }

        @Override
        public RegisterTime.Builder defRegisterTime() {
            return RegisterTime.builder()
                    .registerAt(OperateStatus.START);
        }
    },

    TASK_NODE("taskNode", "任务的某个节点", ContextStoreType.IN_AGENT) {
        @Override
        public boolean checkPersistence(boolean taskFinish) {
            return false;
        }

        @Override
        public RegisterTime.Builder defRegisterTime() {
            return RegisterTime.builder()
                    .registerAt(TaskExecStatus.SUBMITTED);
        }
    },

    ;

    private final String code;
    private final String desc;

    private final ContextStoreType storeType;


    public abstract boolean checkPersistence(boolean taskFinish);


    public abstract RegisterTime.Builder defRegisterTime();

}
