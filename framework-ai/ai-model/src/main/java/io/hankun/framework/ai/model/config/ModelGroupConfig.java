package io.hankun.framework.ai.model.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ModelGroupConfig
 * @createAt: 2025/11/11 09:22
 * @author: hankun
 */
@Data
public class ModelGroupConfig {
    private String protocol;
    private String apiKey;
    private String baseUrl;
    private List<String> models = new ArrayList<>();
    private Map<String, String> configs = new HashMap<>();
}
