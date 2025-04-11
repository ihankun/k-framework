package io.ihankun.framework.db.build.druid.config;

import io.ihankun.framework.db.build.druid.DruidConfig;
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
@ConfigurationProperties(value = "kun.ds.druid")
public class DsDruidConfig {
    private List<DruidConfig> all = new ArrayList<>();
    private List<DruidConfig> domains = new ArrayList<>();
    private List<DruidConfig> services = new ArrayList<>();
    private List<DruidConfig> sds = new ArrayList<>();
    private List<DruidConfig> sus = new ArrayList<>();
}
