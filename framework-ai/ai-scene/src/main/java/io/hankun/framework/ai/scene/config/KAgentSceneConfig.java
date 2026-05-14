package io.hankun.framework.ai.scene.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: KAgentSceneConfig
 * @createAt: 2025/9/5 08:46
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "k.ai.agent.scene")
public class KAgentSceneConfig {

    private Boolean enableRerank = true;

    private Integer similarityTopK = 15;

    private Float similarityThreshold = 0.1f;

    private Integer rerankTopK = 10;

    private Float rerankThreshold = 0.1f;

    private Float directSceneThreshold = 0.95f;
}
