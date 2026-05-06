package io.hankun.framework.ai.mem.config;

import lombok.Data;

/**
 * @description:
 * @className: MemSummaryConfig
 * @createAt: 2025/12/4 14:05
 * @author: hankun
 */
@Data
public class MemSummaryConfig {
    private String tag;
    private Integer summaryCount = 10;
    private Integer summaryDays = 3;
    private boolean must = false;


    public static MemSummaryConfig build(String tag) {
        MemSummaryConfig memSummaryConfig = new MemSummaryConfig();
        memSummaryConfig.setTag(tag);
        return memSummaryConfig;
    }
}