package io.hankun.framework.ai.model.options;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MsunChatOptions
 * @createAt: 2025/7/17 11:26
 * @author: hankun
 */
@Getter
@Builder
public class KChatOptions {

    private final String model;

    private final Integer maxTokens;

    private final List<String> stop;

    private final Double temperature;

    private final Integer topK;

    private final Double topP;

    private final Boolean enableThinking;

    private final Boolean steam;

    private final Boolean streamUsage;


    private final Double presencePenalty;

    private final Double frequencyPenalty;

    private final Boolean parallelToolCalls;

    private final String toolChoiceKey;

    private final String toolChoiceData;

    private final Map<String, String> httpHeaders;
}
