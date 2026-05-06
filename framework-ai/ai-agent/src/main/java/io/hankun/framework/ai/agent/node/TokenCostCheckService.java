package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.common.record.DetailMeta;
import io.hankun.framework.ai.common.record.RecordHolder;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.detail.ChatModelRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: TokenCostCheckService
 * @createAt: 2025/11/6 20:01
 * @author: hankun
 */
@Slf4j
@Component
public class TokenCostCheckService {

    public boolean check(ContextAccess contextAccess) {
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        if (modelRecordContext == null) {
            return true;
        }
        long inputTokens = 0;
        long outputTokens = 0;
        for (RecordHolder<ChatModelRecord> record : modelRecordContext.chatModelRecords()) {
            Integer input = DetailMeta.getInputTokens(record.meta());
            if (input != null) {
                inputTokens += input;
            }
            Integer output = DetailMeta.getOutputTokens(record.meta());
            if (output != null) {
                outputTokens += output;
            }
            Integer inputOffset = DetailMeta.getInputOffset(record.meta());
            if (inputOffset != null) {
                inputTokens -= inputOffset;
            }
            Integer outputOffset = DetailMeta.getOutputOffset(record.meta());
            if (outputOffset != null) {
                outputTokens -= outputOffset;
            }
        }
        log.info("token消耗统计: inputTokens: {}, outputTokens: {}", inputTokens, outputTokens);
        return inputTokens + outputTokens * 2 < 100_000;
    }
}
