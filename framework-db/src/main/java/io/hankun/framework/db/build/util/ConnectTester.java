package io.hankun.framework.db.build.util;

import io.hankun.framework.db.build.connect.entity.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author hankun
 */
@Slf4j
public class ConnectTester {

    public ConnectResult tryConnect(ConnectionConfig connectionConfig, String pass) {
        try {
            Properties info = new Properties();
            info.put("user", connectionConfig.getUsername());
            info.put("password", pass);
            info.put("loginTimeout", "3");
            Connection connection = DriverManager.getConnection(connectionConfig.getUrl(), info);
            return new ConnectResult(connection);
        } catch (SQLException e) {
            return new ConnectResult(e.getSQLState(), e.getMessage(), e);
        }
    }
}
