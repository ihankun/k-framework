package io.ihankun.framework.db.build.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "kun.ch.ds")
public class ChConfig {

    public static final String CH_URL_SUFFIX = "";


    private Map<String, String> datasource = new HashMap<>();

    @Value("${kun.ch.datasource.url.suffix:}")
    private String urlSuffix = CH_URL_SUFFIX;
}
