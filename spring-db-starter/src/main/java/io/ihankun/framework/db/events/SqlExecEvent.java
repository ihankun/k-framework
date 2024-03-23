package io.ihankun.framework.db.events;

import io.ihankun.framework.common.v1.context.DomainContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author hankun
 */
@Getter
public class SqlExecEvent extends ApplicationEvent {
    private final String sqlId;

    private final String domain;

    private final Long operateId;


    public SqlExecEvent(Object source, String sqlId, String domain, Long id) {
        super(source);
        this.sqlId = sqlId;
        this.domain = domain;
        this.operateId = id;
    }

    public String buildKey() {
        return buildKey(domain, sqlId);
    }

    public static String buildKey(String domain, String sqlId) {
        return domain + "$" + sqlId;
    }

    public static SqlExecEvent buildSqlEvent(Object source, String sqlId, Long id) {
        return new SqlExecEvent(source, sqlId, DomainContext.get(), id);
    }
}
