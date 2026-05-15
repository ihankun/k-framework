package io.hankun.framework.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "k.redis.domain.ignore")
@RefreshScope
public class RedisDomainIgnoreProperties {
    /**
     * 默认不启用
     */
    private boolean enable = false;

    /**
     * 忽略隔离的域名列表，不在列表中的域名将进行逻辑隔离
     */
    private List<String> domains = new ArrayList<>();

    /**
     * 忽略隔离的key列表，不在列表中的key将进行逻辑隔离
     */
    private List<String> keys = new ArrayList<>();
}
