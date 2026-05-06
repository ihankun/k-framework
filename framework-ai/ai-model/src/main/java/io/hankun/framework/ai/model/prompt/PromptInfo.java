package io.hankun.framework.ai.model.prompt;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: PromptInfo
 * @createAt: 2025/10/24 17:06
 * @author: hankun
 */
public record PromptInfo(String prompt, List<String> params, Map<String, String> paramsData) {
}
