package io.ihankun.framework.db.dynamic.bean;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author hankun
 */
@Data
public class DataSourceBuildProperty {

    /**
     * 域名
     */
    private final String domain;
    /**
     * 数据源别名
     */
    private final String alias;
    /**
     * 数据库标识
     */
    private final String dbMark;
    /**
     * 数据库加密用环境key
     */
    private final String key;

    public DataSourceBuildProperty(String domain, String alias, String dbMark, String key) {
        this.domain = domain;
        this.alias = alias;
        this.dbMark = dbMark;
        this.key = key;
    }


    public String getDomain() {
        return domain;
    }

    public String getAlias() {
        return alias;
    }

    public String getKey() {
        return StringUtils.isEmpty(key) ? "empty" : "withValue";
    }

    public String getEnvKey() {
        return key;
    }
}
