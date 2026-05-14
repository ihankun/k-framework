package io.hankun.framework.ai.store.memory.shortTerm;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.content.Media;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KMessageDeserializer
 * @createAt: 2025/7/1 17:57
 * @author: hankun
 */
@Slf4j
public class KMessageDeserializer extends JsonDeserializer<Message> {

    enum Field {

        TEXT("text"),

        TOOL_CALLS("toolCalls"),

        METADATA("metadata"),

        MEDIA("media"),

        RESPONSES("responses");

        final String name;

        Field(String name) {
            this.name = name;
        }

    }

    @Override
    public Message deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);

        log.debug("Deserializing message: {}", node);


        if (node.isTextual()) {
            return new UserMessage(node.asText());
        }

        String type = readType(node);

        String text = readText(node);

        Map<String, Object> metadata = readMetadata(mapper, node);

        switch (type) {
            case "ASSISTANT" -> {
                List<AssistantMessage.ToolCall> requests = readToolCalls(mapper, node);
                return new AssistantMessage(text, metadata, requests);
            }
            case "SYSTEM" -> {
                return new SystemMessage(text);
            }
            case "TOOL" -> {
                List<ToolResponseMessage.ToolResponse> toolResponses = readToolResponses(mapper, node);
                return new ToolResponseMessage(toolResponses, metadata);
            }
            case null, default -> {
                return new UserMessage(text);
            }
        }
    }

    /**
     * read message type from JsonNode
     */
    private String readType(JsonNode parentNode) {
        JsonNode node = parentNode.findValue("messageType");
        if (node == null || node.isNull()) {
            node = parentNode.findValue("type");
        }
        if (node == null || node.isNull()) {
            node = parentNode.findValue("role");
        }
        if (node == null || node.isNull()) {
            log.warn("Message type not found, defaulting to USER");
            return "USER";
        }
        return node.asText();
    }

    private String readText(JsonNode parentNode) {
        JsonNode node = parentNode.findValue(Field.TEXT.name);
        if (node == null || node.isNull()) {
            return "";
        }
        return node.asText();
    }

    private Map<String, Object> readMetadata(ObjectMapper mapper, JsonNode parentNode)
            throws JsonProcessingException {
        JsonNode node = parentNode.findValue(Field.METADATA.name);

        if (node == null || node.isNull()) {
            return Map.of();
        }
        if (!node.isObject()) {
            throw new IllegalStateException("Metadata must be an object");
        }
        return mapper.treeToValue(node, new TypeReference<>() {
        });
    }

    private List<AssistantMessage.ToolCall> readToolCalls(ObjectMapper mapper, JsonNode parentNode)
            throws JsonProcessingException {
        JsonNode node = parentNode.findValue(Field.TOOL_CALLS.name);

        if (node == null || node.isNull()) {
            return List.of();
        }
        if (!node.isArray()) {
            throw new IllegalStateException("Tool calls must be an array");
        }
        List<AssistantMessage.ToolCall> toolCalls = new LinkedList<>();

        for (JsonNode requestNode : node) {
            toolCalls.add(mapper.treeToValue(requestNode, new TypeReference<>() {
            }));
        }
        return toolCalls;
    }

    private List<Media> readMedia(ObjectMapper mapper, JsonNode parentNode)
            throws JsonProcessingException {
        JsonNode node = parentNode.findValue(Field.MEDIA.name);
        if (node == null || node.isNull()) {
            return List.of();
        }
        if (!node.isArray()) {
            throw new IllegalStateException("Media must be an array");
        }
        List<Media> media = new LinkedList<>();
        for (JsonNode mediaNode : node) {
            media.add(mapper.treeToValue(mediaNode, new TypeReference<>() {
            }));
        }
        return media;
    }

    private List<ToolResponseMessage.ToolResponse> readToolResponses(ObjectMapper mapper, JsonNode parentNode)
            throws JsonProcessingException {
        JsonNode node = parentNode.findValue(Field.RESPONSES.name);
        if (node == null || node.isNull()) {
            return List.of();
        }
        if (!node.isArray()) {
            throw new IllegalStateException("Tool responses must be an array");
        }
        List<ToolResponseMessage.ToolResponse> toolResponses = new LinkedList<>();
        for (JsonNode toolResponseNode : node) {
            toolResponses.add(mapper.treeToValue(toolResponseNode, new TypeReference<>() {
            }));
        }
        return toolResponses;
    }
}
