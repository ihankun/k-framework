package io.hankun.framework.db.build.ds;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class DataSourceConfig {
    /**
     * 数据源对应域名
     */
    private String domain;
    /**
     * 当前服务名
     */
    private String serviceName;
    /**
     * 数据库标记（HIS_MASTER）
     */
    private String dbMark;
    /**
     * 数据源名称（master）
     */
    private String ds;
    /**
     * 数据库名称（chis）
     */
    private String db;
    /**
     * 数据库用户名（comm_app）
     */
    private String user;
    /**
     * 数据库模式（comm），模板中该值为空
     */
    private String schema;
    /**
     * 数据库协议（jdbc:postgresql）
     */
    private String protocol;
    /**
     * 数据库参数（useUnicode=true&characterEncoding=utf8）
     */
    private String parameter;
    /**
     * 数据库驱动（org.postgresql.Driver）
     */
    private String driver;
    /**
     * seata配置（true）
     */
    private boolean seata = true;
}
