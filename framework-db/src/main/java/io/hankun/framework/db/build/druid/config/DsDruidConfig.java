package io.hankun.framework.db.build.druid.config;

import io.hankun.framework.db.build.druid.KDruidConfig;
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
    private List<KDruidConfig> all = new ArrayList<>();
    private List<KDruidConfig> domains = new ArrayList<>();
    private List<KDruidConfig> services = new ArrayList<>();
    private List<KDruidConfig> sds = new ArrayList<>();
    private List<KDruidConfig> sus = new ArrayList<>();
}
