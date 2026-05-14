package io.hankun.framework.commons.http;

import lombok.Data;

import java.util.Map;

/**
 * @description:
 * @className: KConnectorConfig
 * @createAt: 2026/1/4 11:15
 * @author: hankun
 */
@Data
public class KConnectorConfig {
    private String code;
    private String type;
    private Integer connectTimeoutS = 5;
    private Integer readTimeoutS = 20;
    private Integer writeTimeoutS = 5;
    private Integer maxConnections = 20;
    private Integer maxIdleTimeS = 60;
    private Map<String, Object> extraProperties;
}
