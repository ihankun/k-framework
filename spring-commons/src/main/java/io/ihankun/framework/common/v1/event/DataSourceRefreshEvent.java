package io.ihankun.framework.common.v1.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author hankun
 */
@Getter
public class DataSourceRefreshEvent extends ApplicationEvent {

    private final String dbMark;

    private final String domain;

    public DataSourceRefreshEvent(Object source, String dbMark, String domain) {
        super(source);
        this.dbMark = dbMark;
        this.domain = domain;
    }
}
