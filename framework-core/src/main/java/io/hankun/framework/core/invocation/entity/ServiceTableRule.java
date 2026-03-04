package io.hankun.framework.core.invocation.entity;

import lombok.Data;

import java.util.List;


/**
 * @author hankun
 */
@Data
public class ServiceTableRule {
    /**
     * 是否开启
     */
    private Boolean enable;
    /**
     * 数据库配置
     */
    private List<DbRule> dbs;

    @Data
    public static class DbRule {
        /**
         * 数据库名
         */
        private String db;
        /**
         * schema配置
         */
        private List<SchemaRule> schemas;
    }

    @Data
    public static class SchemaRule {
        /**
         * schema名
         */
        private String schema;
        /**
         * schema全局权限
         */
        private Integer auth;
        /**
         * 表权限配置
         */
        private List<TableRule> tables;
    }

    @Data
    public static class TableRule {
        /**
         * 表名
         */
        private String table;
        /**
         * 表权限
         */
        private Integer auth;
    }
}
