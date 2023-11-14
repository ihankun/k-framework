package io.ihankun.framework.db.dynamic;

import org.springframework.context.ApplicationEvent;

import javax.sql.DataSource;

/**
 * @author hankun
 */
public class DataSourceBuildEvent extends ApplicationEvent {

    private final DataSource dataSource;

    public DataSourceBuildEvent(Object source, DataSource dataSource) {
        super(source);
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }
}
