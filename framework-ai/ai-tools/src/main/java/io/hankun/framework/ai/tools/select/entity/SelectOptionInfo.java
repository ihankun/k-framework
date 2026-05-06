package io.hankun.framework.ai.tools.select.entity;

import io.hankun.framework.ai.tools.select.AiSelectConfig;

/**
 * @description:
 * @className: SelectOptionInfo
 * @createAt: 2025/9/2 16:18
 * @author: hankun
 */
public record SelectOptionInfo<T extends IAiSelectOption>(String key, String desc, String matchInfo, String rerankInfo,
                                                          T value) {

    public static <T extends IAiSelectOption> SelectOptionInfo<T> of(T selectedOption, AiSelectConfig config) {
        String key = selectedOption.key();
        String desc = selectedOption.desc();
        String matchInfo = config.getMatchInfoFormatter().apply(key, desc);
        String rerankInfo = config.getRerankInfoFormatter().apply(key, desc);
        return new SelectOptionInfo<>(key, desc, matchInfo, rerankInfo, selectedOption);
    }

    public static <T extends IAiSelectOption> SelectOptionInfo<T> ofEmpty(String key, String desc, AiSelectConfig config) {
        String matchInfo = config.getMatchInfoFormatter().apply(key, desc);
        String rerankInfo = config.getRerankInfoFormatter().apply(key, desc);
        return new SelectOptionInfo<>(key, desc, matchInfo, rerankInfo, null);
    }
}
