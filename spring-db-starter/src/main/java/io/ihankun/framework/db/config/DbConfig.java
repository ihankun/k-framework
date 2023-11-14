package io.ihankun.framework.db.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * SQL相关配置
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(value = "kun.database.config")
public class DbConfig {
    /**
     * 最大行数限制
     */
    private int maxRows = 10000;
    /**
     * 是否开启SQL检查
     */
    private boolean checkSql = true;
    /**
     * 是否开启增加反统方注释
     */
    private boolean comment = false;
}
