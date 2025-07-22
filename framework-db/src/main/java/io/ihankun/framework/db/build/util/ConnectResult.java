package io.ihankun.framework.db.build.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Connection;

/**
 * @author hankun
 */
@AllArgsConstructor
@Getter
public class ConnectResult {
    private final Connection connection;
    private final String code;
    private final String message;
    private final Throwable throwable;

    public ConnectResult(Connection connection) {
        this(connection, "", "", null);
    }

    public ConnectResult(String code, String message, Throwable throwable) {
        this(null, code, message, throwable);
    }
}
