//package io.ihankun.framework.db.build.ds;
//
//import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
//import io.ihankun.framework.db.build.connect.entity.ConnectionConfig;
//import io.ihankun.framework.db.build.druid.DruidConfigBuilder;
//import io.ihankun.framework.db.build.util.PropertyHelper;
//import io.ihankun.framework.db.dynamic.DataSourceBuildEvent;
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//
///**
// * @author hankun
// */
//@Slf4j
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "false", matchIfMissing = true)
//@Component
//public class DataSourceCreator implements ApplicationEventPublisherAware {
//
//    private final DruidConfigBuilder druidConfigBuilder;
//
//
//    private final DefaultDataSourceCreator creator;
//
//    private ApplicationEventPublisher applicationEventPublisher;
//
//    public DataSourceCreator(DruidConfigBuilder druidConfigBuilder, DefaultDataSourceCreator creator) {
//        this.druidConfigBuilder = druidConfigBuilder;
//        this.creator = creator;
//    }
//
//
//    public DataSource create(DataSourceConfig dataSourceConfig, ConnectionConfig config) {
//        try {
//            DataSourceProperty property = buildDataSourceProperty(dataSourceConfig, config);
//            DataSource dataSource = creator.createDataSource(property);
//            applicationEventPublisher.publishEvent(new DataSourceBuildEvent(this, dataSource));
//            log.info("数据源初始化成功，name={}，property={}", property.getPoolName(), PropertyHelper.buildOutput(property));
//            return dataSource;
//        } catch (Exception e) {
//            log.error("创建数据源失败,dsName={},ds={},url={},e=", dataSourceConfig.getDs(), dataSourceConfig,
//                    config.getUrl(), e);
//            throw new DbException("数据源创建失败：" + e.getMessage());
//        }
//    }
//
//    public DataSourceProperty buildDataSourceProperty(DataSourceConfig dataSourceConfig, ConnectionConfig config) {
//        String poolName = dataSourceConfig.getDs() + "_" + dataSourceConfig.getDomain();
//        return PropertyHelper.buildProperty(config, poolName, druidConfigBuilder.createDruidConfig(dataSourceConfig)
//                , dataSourceConfig.isSeata());
//    }
//
//
//    @Override
//    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//}
