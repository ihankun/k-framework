package io.ihankun.framework.db.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * SQL相关配置
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(value = "spring.database.config")
public class DbConfig {
    /**
     * 最大行数限制
     */
    private int maxRows = 10000;

    /**
     * 缓存MapperId，优化性能
     */
    private boolean mapperIdCache = true;

    /**
     * 是否开启SQL检查
     */
    private boolean updateTimeCheck = true;

    /**
     * 是否开启增加反统方注释
     */
    private boolean fanTongFangComment = false;

    /**
     * 按照域名开启反统方注释
     */
    private List<String> fanTongFangCommentByDomains;

    /**
     * 是否增加mapper的注释
     */
    private boolean mapperComment = true;

    /**
     * 是否打印mapper的日志
     */
    private boolean mapperCommentLog = false;

    /**
     * 熔断SQL集合
     */
    private List<String> breakSql;

    /**
     * 慢SQL打印
     */
    private boolean slowSql = true;


    /**
     * 构建Properties类型字符串
     *
     * @return re
     */
    public String toPropertiesStr() {
        StringBuilder builder = new StringBuilder("#数据库自定义策略配置\n");
        builder.append("#最大行数限制\n").append("kun.database.config.maxRows=").append(maxRows).append("\n");
        builder.append("#sys_update_time校验开关\n").append("kun.database.config.updateTimeCheck=").append(updateTimeCheck).append("\n");
        builder.append("#反统方注释开关\n").append("kun.database.config.fanTongFangComment=").append(fanTongFangComment).append("\n");
        builder.append("#mapper定位注释开关\n").append("kun.database.config.mapperComment=").append(mapperComment).append("\n");
        builder.append("#熔断SQL列表\n");
        if (!CollectionUtils.isEmpty(breakSql)) {
            for (int i = 0; i < breakSql.size(); i++) {
                builder.append("kun.database.config.breakSql[").append(i).append("]=").append(breakSql.get(i));
                if (i != breakSql.size() - 1) {
                    builder.append("\n");
                }
            }
        }
        return builder.toString();
    }
}
