package io.hankun.framework.ai.agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: KNodeConfig
 * @createAt: 2025/10/23 21:21
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "k.ai.node")
public class KNodeConfig {
    private final Map<String, NodeConfig> actionConfig = new HashMap<>();

    private final NodeConfig common = new NodeConfig();


    @Data
    public static class NodeConfig {
        private String model;
        private Boolean stream;
    }

    public NodeConfig loadConfig(String nodeId) {
        NodeConfig nodeConfig = actionConfig.get(nodeId);
        if (nodeConfig == null) {
            nodeConfig = common;
        }
        NodeConfig result = new NodeConfig();
        result.setModel(nodeConfig.getModel());
        result.setStream(nodeConfig.getStream());
        if (nodeConfig.getModel() == null) {
            result.setModel(common.getModel());
        }
        if (nodeConfig.getStream() == null) {
            result.setStream(common.getStream());
        }
        return result;
    }


}
