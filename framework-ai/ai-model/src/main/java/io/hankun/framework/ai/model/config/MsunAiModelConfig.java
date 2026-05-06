package io.hankun.framework.ai.model.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @description:
 * @className: MsunAiModelConfig
 * @createAt: 2025/7/3 09:50
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "msun.ai.model")
public class MsunAiModelConfig {
    private Boolean enableRerank = false;
    private String defChat;
    private String defEmbedding;
    private String defRerank;
    private List<ModelGroupConfig> modelGroups;
}
