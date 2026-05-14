package io.hankun.framework.ai.model.options;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KChatOptionsBuildService
 * @createAt: 2025/7/17 11:56
 * @author: hankun
 */
@Component
public class KChatOptionsBuildService {

    private final Map<Class<?>, OptionsConvertor<?, ?>> optionConvertorMap;

    private final Map<Class<?>, OptionsConvertor<?, ?>> modelConvertorMap;

    public KChatOptionsBuildService(List<OptionsConvertor<?, ?>> optionsConvertors) {
        this.optionConvertorMap = new HashMap<>(optionsConvertors.size());
        this.modelConvertorMap = new HashMap<>(optionsConvertors.size());
        for (OptionsConvertor<?, ?> optionsConvertor : optionsConvertors) {
            optionConvertorMap.put(optionsConvertor.getOptionClass(), optionsConvertor);
            modelConvertorMap.put(optionsConvertor.getModelClass(), optionsConvertor);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ChatOptions> T buildByOption(Class<T> optionClass, KChatOptions option) {
        OptionsConvertor<T, ?> optionsConvertor = (OptionsConvertor<T, ?>) optionConvertorMap.get(optionClass);
        if (optionsConvertor == null) {
            throw new IllegalArgumentException("No option convertor found for option class: " + optionClass);
        }
        return optionsConvertor.convert(option);
    }

    @SuppressWarnings("unchecked")
    public <M extends ChatModel> ChatOptions buildByModel(Class<? extends ChatModel> modelClass, KChatOptions option) {
        OptionsConvertor<?, M> optionsConvertor = (OptionsConvertor<?, M>) modelConvertorMap.get(modelClass);
        if (optionsConvertor == null) {
            throw new IllegalArgumentException("No option convertor found for model class: " + modelClass);
        }
        return optionsConvertor.convert(option);
    }
}
