package io.hankun.framework.ai.mcp.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KAiMcpConfig
 * @createAt: 2025/6/5 14:21
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "k.ai.mcp")
public class KAiMcpConfig {

    private String registerType = "nacos";

    private String redisPrefix = "";

    private String defGray;

    private List<String> services = new ArrayList<>();

    private Map<String, String> path = new HashMap<>();

    private Map<String, String> clusterName = new HashMap<>();

    private Map<String, String> groupName = new HashMap<>();

    private Map<String, String> namespaceId = new HashMap<>();
}
