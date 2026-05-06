package io.hankun.framework.ai.tools.trace;

import io.hankun.framework.ai.common.record.DetailMeta;
import io.hankun.framework.ai.common.session.task.MapConfig;
import io.hankun.framework.ai.store.history.detail.ChatModelRecord;
import io.hankun.framework.commons.utils.ContextUtil;
import org.springframework.ai.chat.messages.Message;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: TraceContext
 * @createAt: 2025/10/17 18:11
 * @author: hankun
 */
public record TraceContext(String type, Map<String, Object> meta, MapConfig mapConfig) {

    public void setInputTokens(Integer inputTokens) {
        DetailMeta.setInputTokens(meta, inputTokens);
    }

    public void setInputOffset(Integer inputOffset) {
        DetailMeta.setInputOffset(meta, inputOffset);
    }

    public void setOutputTokens(Integer outputTokens) {
        DetailMeta.setOutputTokens(meta, outputTokens);
    }

    public void setOutputOffset(Integer outputOffset) {
        DetailMeta.setOutputOffset(meta, outputOffset);
    }

    public void setTotalTokens(Integer totalTokens) {
        meta.put("totalTokens", totalTokens);
    }

    public void setFirstTokenTime(Long firstTokenTime) {
        meta.computeIfAbsent("firstTokenTime", k -> firstTokenTime);
    }


    public void setModelName(String modelName) {
        meta.put("modelName", modelName);
    }

    public void start() {
        DetailMeta.setStart(meta);
    }

    public boolean isEnd() {
        return DetailMeta.getEnd(meta) != null;
    }

    public void end() {
        DetailMeta.setEnd(meta);
    }

    public void setOutputMessages(List<Message> messages) {
        ContextUtil.setData(meta, ChatModelRecord.OUTPUTS, messages);
    }

}
