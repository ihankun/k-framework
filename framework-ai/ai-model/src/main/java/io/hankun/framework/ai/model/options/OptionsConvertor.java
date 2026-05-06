package io.hankun.framework.ai.model.options;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;

/**
 * @description:
 * @className: OptionsConvertor
 * @createAt: 2025/7/17 11:56
 * @author: hankun
 */
public interface OptionsConvertor<T extends ChatOptions, M extends ChatModel> {

    Class<T> getOptionClass();

    Class<M> getModelClass();

    T convert(KChatOptions option);
}
