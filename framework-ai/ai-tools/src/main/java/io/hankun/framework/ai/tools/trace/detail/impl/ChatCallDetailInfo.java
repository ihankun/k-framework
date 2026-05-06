package io.hankun.framework.ai.tools.trace.detail.impl;

import io.hankun.framework.ai.tools.trace.detail.BaseDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.DetailLevel;
import io.hankun.framework.ai.tools.trace.detail.DetailType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: ChatCallDetailInfo
 * @createAt: 2025/6/30 13:48
 * @author: hankun
 */
@Getter
@Setter
public class ChatCallDetailInfo extends BaseDetailInfo {

    private String modelName;

    private Integer inputTokens;

    private Integer inputOffset = 0;

    private Integer outputTokens;

    private Integer outputOffset = 0;

    private final List<Message> inputMessages = new ArrayList<>();

    private final List<Message> outputMessages = new ArrayList<>();

    private final List<ResponseNode> resultNodes = new ArrayList<>();

    private StringBuilder resultMessage = new StringBuilder();

    private final List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();

    public ChatCallDetailInfo(String conversationId, DetailLevel level) {
        super(DetailType.CHAT_CALL, conversationId, level);
    }

    @Override
    public String groupKey() {
        return modelName;
    }

    @Override
    public String buildDetailDesc() {
        return "模型：" + modelName + "，输入：" + inputTokens + "，输出：" + outputTokens;
    }

    public void start(ChatCallDetailInfo callDetailInfo, ToolExecutionResult result) {
        this.modelName = callDetailInfo.getModelName();
        if (detailLevel.detailThan(DetailLevel.DETAIL)) {
            this.inputMessages.clear();
            this.inputMessages.addAll(result.conversationHistory());
        }
        this.inputOffset = callDetailInfo.getInputTokens();
        this.outputOffset = callDetailInfo.getOutputTokens();
        this.start();
    }

    public void start(ChatClientRequest chatRequest) {
        ChatOptions options = chatRequest.prompt().getOptions();
        if (options != null) {
            modelName = options.getModel();
        }
        if (detailLevel.detailThan(DetailLevel.DETAIL)) {
            Prompt prompt = chatRequest.prompt();
            inputMessages.addAll(prompt.getInstructions());
        }
        start();
    }

    public void finish(ChatResponse chatResponse) {
        if (chatResponse == null) {
            return;
        }
        ChatResponseMetadata responseMetadata = chatResponse.getMetadata();
        if (responseMetadata == null) {
            return;
        }
        Usage usage = responseMetadata.getUsage();
        if (usage == null) {
            return;
        }
        if (StringUtils.hasText(responseMetadata.getModel())) {
            setModelName(responseMetadata.getModel());
        }
        setInputTokens(usage.getPromptTokens());
        setOutputTokens(usage.getCompletionTokens());
        end();
        for (Generation generation : chatResponse.getResults()) {
            AssistantMessage assistantMessage = generation.getOutput();
            ChatGenerationMetadata metadata = generation.getMetadata();
            resultMessage.append(assistantMessage.getText());
            if (detailLevel.detailThan(DetailLevel.BASE)) {
                ResponseNode node = new ResponseNode(assistantMessage.getText());
                node.setFinishReason(metadata.getFinishReason());
                resultNodes.add(node);
                if (assistantMessage.hasToolCalls()) {
                    toolCalls.addAll(assistantMessage.getToolCalls());
                }
            }
            if (detailLevel.detailThan(DetailLevel.DETAIL)) {
                outputMessages.add(assistantMessage);
            }
        }

    }

    @Getter
    @Setter
    public static class ResponseNode {

        private final String content;

        private final long responseTime;

        private String finishReason;

        public ResponseNode(String content) {
            this.content = content;
            this.responseTime = System.currentTimeMillis();
        }
    }
}
