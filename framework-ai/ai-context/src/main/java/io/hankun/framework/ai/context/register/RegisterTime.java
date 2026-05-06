package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.common.session.OperateStatus;
import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.common.session.node.TaskNodeStatus;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: RegisterTime
 * @createAt: 2025/10/16 14:32
 * @author: hankun
 */
public record RegisterTime(List<SessionStatus> sessionStatuses,
                           List<OperateStatus> operateStatus,
                           List<TaskExecStatus> taskStatus,
                           List<TaskNodeStatus> nodeStatus,
                           boolean contextBuild) {

    public static RegisterTime notAutoRegister() {
        return new Builder().build();
    }

    public static RegisterTime whenContextBuild() {
        return new Builder().registerAtContextBuild().build();
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private final List<SessionStatus> sessionStatuses = new ArrayList<>();
        private final List<OperateStatus> operateStatus = new ArrayList<>();
        private final List<TaskExecStatus> taskStatus = new ArrayList<>();
        private final List<TaskNodeStatus> nodeStatus = new ArrayList<>();
        private boolean contextBuild = false;

        public Builder() {
        }

        public Builder registerAt(SessionStatus sessionStatus) {
            this.sessionStatuses.add(sessionStatus);
            return this;
        }

        public Builder registerAt(OperateStatus operateStatus) {
            this.operateStatus.add(operateStatus);
            return this;
        }

        public Builder registerAt(TaskExecStatus taskExecStatus) {
            this.taskStatus.add(taskExecStatus);
            return this;
        }

        public Builder registerAt(TaskNodeStatus taskNodeStatus) {
            this.nodeStatus.add(taskNodeStatus);
            return this;
        }

        public Builder registerAtContextBuild() {
            this.contextBuild = true;
            return this;
        }

        public RegisterTime build() {
            return new RegisterTime(sessionStatuses, operateStatus, taskStatus, nodeStatus, contextBuild);
        }
    }

    public boolean checkRegister(SessionStatus sessionStatus) {
        return this.sessionStatuses.contains(sessionStatus);
    }

    public boolean checkRegister(OperateStatus operateStatus) {
        return this.operateStatus.contains(operateStatus);
    }

    public boolean checkRegister(TaskExecStatus taskExecStatus) {
        return this.taskStatus.contains(taskExecStatus);
    }

    public boolean checkRegister(TaskNodeStatus taskNodeStatus) {
        return this.nodeStatus.contains(taskNodeStatus);
    }

}
