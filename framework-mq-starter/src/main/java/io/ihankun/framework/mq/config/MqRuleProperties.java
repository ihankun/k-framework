package io.ihankun.framework.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

/**
 * @author hankun
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ihankun.mq.rule")
public class MqRuleProperties {

    /**
     * 是否开启
     */
    private boolean enabled = false;
    /**
     * topic 映射
     */
    private Map<String, Set<String>> topics;
}
