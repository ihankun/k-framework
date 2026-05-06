package io.hankun.framework.ai.tools.select.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: SelectBy
 * @createAt: 2025/9/2 17:51
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum SelectBy {

    DIRECT_MATCH("directMatch", "直接匹配"),

    RERANK_MATCH("rerankMatch", "rerank匹配"),

    AI_CHOOSE("aiChoose", "ai选择"),

    NOT_FIND("notFind", "未找到"),

    ONLY_ONE("onlyOne", "唯一且必须选择一个"),

    AI_FAILED("aiFailed", "ai失败"),
    ;

    private final String name;
    private final String desc;
}
