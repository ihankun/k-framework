package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.context.register.RegisterTime;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: HumanFeedbackContextRegister
 * @createAt: 2025/10/27 16:01
 * @author: hankun
 */
@Component
class HumanFeedbackContextRegister implements ContextRegister<HumanFeedbackContext> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.TASK_IN_AGENT;
    }

    @Override
    public String desc() {
        return "用户反馈";
    }

    @NotNull
    @Override
    public Class<HumanFeedbackContext> dataType() {
        return HumanFeedbackContext.class;
    }

    @Override
    public HumanFeedbackContext build(@NotNull CurrentId currentId, HumanFeedbackContext old,
                                      @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        return null;
    }

    @NotNull
    @Override
    public RegisterTime registerTime() {
        return RegisterTime.notAutoRegister();
    }


    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId,
                             HumanFeedbackContext data, ContextAccess contextAccess) {
        if (data == null) {
            return;
        }
        if (TaskExecStatus.WORKING.equals(taskExecStatus)) {
            InputParams inputParams = contextAccess.getData(InputParams.class);
            data.setFeedbackData(inputParams.data());
            data.setFeedback(inputParams.formatInput());
        }
    }
}
