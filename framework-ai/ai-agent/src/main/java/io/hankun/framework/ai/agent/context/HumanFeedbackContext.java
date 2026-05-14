package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.memory.shortTerm.MessageConvertor;
import lombok.Data;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: HumanFeedbackContext
 * @createAt: 2025/10/27 16:00
 * @author: hankun
 */
@Data
public class HumanFeedbackContext implements IContext {
    private List<String> messages;
    private String nodeId;
    private String required;
    private String feedback;
    private Map<String, Object> feedbackData;

    public List<Message> listMessage() {
        List<Message> list = new ArrayList<>(messages.size());
        for (String message : messages) {
            list.add(MessageConvertor.clearMeta(MessageConvertor.convert(message)));
        }
        return list;
    }

    public void putMessages(List<Message> messages, String nodeId) {
        this.messages = new ArrayList<>(messages.size());
        this.nodeId = nodeId;
        for (Message message : messages) {
            this.messages.add(MessageConvertor.convert(message));
        }
    }

    public static void createFeedback(ContextAccess contextAccess, CurrentId currentId, String required) {
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        HumanFeedbackContext human = new HumanFeedbackContext();
        human.setMessages(new ArrayList<>());
        human.setRequired(required);
        human.setFeedback("");
        human.putMessages(modelRecordContext.fetchCurrentMessage(currentId), currentId.nodeId());
        contextAccess.setData(human);
    }

    public void clear() {
        this.messages.clear();
        this.feedback = "";
        this.required = "";
        this.feedbackData.clear();
    }
}
