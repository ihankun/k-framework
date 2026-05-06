package io.hankun.framework.ai.tools.model.options;


import io.hankun.framework.ai.model.options.KChatOptions;
import io.hankun.framework.ai.model.options.OptionsConvertor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: OpenAiOptionsConvertor
 * @createAt: 2025/7/17 13:42
 * @author: hankun
 */
@Component
public class OpenAiOptionsConvertor implements OptionsConvertor<OpenAiChatOptions, OpenAiChatModel> {
    @Override
    public Class<OpenAiChatOptions> getOptionClass() {
        return OpenAiChatOptions.class;
    }

    @Override
    public Class<OpenAiChatModel> getModelClass() {
        return OpenAiChatModel.class;
    }

    @Override
    public OpenAiChatOptions convert(KChatOptions option) {
        return OpenAiChatOptions.builder()
                .model(option.getModel())
                .toolChoice(toolChoice(option))
                .maxTokens(option.getMaxTokens())
                .stop(option.getStop())
                .temperature(option.getTemperature())
                .topP(option.getTopP())
                .streamUsage(true)
                .presencePenalty(option.getPresencePenalty())
                .frequencyPenalty(option.getFrequencyPenalty())
                .build();
    }

    private Object toolChoice(KChatOptions options) {
        return options.getToolChoiceData();
    }
}
