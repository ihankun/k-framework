package io.ihankun.framework.db.build.connect.config;


import io.ihankun.framework.db.build.connect.entity.DsOverriderConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(value = "kun.ds.override")
public class DsOverrideConfig {
    private List<DsOverriderConfig> all = new ArrayList<>();
    private List<DsOverriderConfig> domains = new ArrayList<>();
    private List<DsOverriderConfig> services = new ArrayList<>();
    private List<DsOverriderConfig> sds = new ArrayList<>();
    private List<DsOverriderConfig> sus = new ArrayList<>();
}
