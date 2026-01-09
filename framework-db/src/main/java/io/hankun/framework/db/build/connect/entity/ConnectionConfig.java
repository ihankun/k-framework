package io.hankun.framework.db.build.connect.entity;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class ConnectionConfig {
    /**
     * JDBC driver
     */
    private String driverClassName;
    /**
     * JDBC url 地址
     */
    private String url;
    /**
     * JDBC 用户名
     */
    private String username;
    /**
     * JDBC 密码
     */
    private String password;
}
