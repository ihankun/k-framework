package io.hankun.framework.ai.store.history.context;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.DetailMeta;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.store.history.po.ModelHistory;
import io.hankun.framework.ai.store.history.repository.ModelHistoryRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @description:
 * @className: ModelRecordContextRegister
 * @createAt: 2025/10/16 19:02
 * @author: hankun
 */
@Component
public class ModelRecordContextRegister implements ContextRegister<ModelRecordContext> {

    private final ModelHistoryRepository modelHistoryRepository;

    public ModelRecordContextRegister(ModelHistoryRepository modelHistoryRepository) {
        this.modelHistoryRepository = modelHistoryRepository;
    }

    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.MESSAGE_IN_AGENT;
    }

    @Override
    public String desc() {
        return "模型记录";
    }


    @NotNull
    @Override
    public Class<ModelRecordContext> dataType() {
        return ModelRecordContext.class;
    }

    @Override
    public ModelRecordContext build(@NotNull CurrentId currentId, ModelRecordContext old,
                                    @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        return ModelRecordContext.of(inputParams.config());
    }

    @NotNull
    @Override
    public String getKey() {
        return "modelRecord";
    }

    @Override
    public void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId, ModelRecordContext data,
                             ContextAccess contextAccess) {
        if (taskExecStatus.isFinal() || TaskExecStatus.INPUT_REQUIRED.equals(taskExecStatus)) {
            List<ModelHistory> modelHistoryList = new ArrayList<>();
            modelHistoryList.addAll(ModelHistory.of(data.chatModelRecords()));
            modelHistoryList.addAll(ModelHistory.of(data.embeddingRecords()));
            modelHistoryList.addAll(ModelHistory.of(data.rerankRecords()));
            modelHistoryList.addAll(ModelHistory.of(data.taskToolCallRecords()));
            modelHistoryList.sort(new Comparator<ModelHistory>() {
                @Override
                public int compare(ModelHistory o1, ModelHistory o2) {
                    Long startTime1 = DetailMeta.getStart(o1.getMeta());
                    Long startTime2 = DetailMeta.getStart(o2.getMeta());
                    return startTime1.compareTo(startTime2);
                }
            });
            modelHistoryRepository.save(modelHistoryList);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
