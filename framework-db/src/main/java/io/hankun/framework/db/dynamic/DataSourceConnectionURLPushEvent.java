package io.hankun.framework.db.dynamic;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author hankun
 */
@Getter
public class DataSourceConnectionURLPushEvent extends ApplicationEvent {

    private final String ip;

    private final int port;

    private final String domain;

    public DataSourceConnectionURLPushEvent(Object source, String ip, int port, String domain) {
        super(source);
        this.ip = ip;
        this.port = port;
        this.domain = domain;
    }
}
