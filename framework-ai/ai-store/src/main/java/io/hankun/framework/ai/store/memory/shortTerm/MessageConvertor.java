package io.hankun.framework.ai.store.memory.shortTerm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.util.CollectionUtils;

/**
 * @description:
 * @className: MessageConvertor
 * @createAt: 2025/10/15 10:46
 * @author: hankun
 */
@Slf4j
public class MessageConvertor {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Message.class, new KMessageDeserializer());
        objectMapper.registerModule(module);
    }

    public static Message convert(String message) {
        try {
            return objectMapper.readValue(message, Message.class);
        } catch (Exception e) {
            log.error("convert String to message error: {}", message);
            throw new RuntimeException(e);
        }
    }

    public static String convert(Message message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("convert message to String error: {}", message);
            throw new RuntimeException(e);
        }
    }

    public static Message clearMeta(Message message) {
        if (message == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(message.getMetadata())) {
            return message;
        }
        message.getMetadata().clear();
        return message;
    }
}
