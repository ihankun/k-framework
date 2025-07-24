//package io.ihankun.framework.db.build.ds;
//
//import io.ihankun.framework.db.build.BuildInfoHolder;
//import io.ihankun.framework.db.build.DsPropertiesProvider;
//import io.ihankun.framework.db.build.config.DsConfigReader;
//import io.ihankun.framework.db.build.connect.ConnectionConfigBuilder;
//import io.ihankun.framework.db.build.connect.entity.ConnectionConfig;
//import lombok.extern.slf4j.Slf4j;
//
//import javax.sql.DataSource;
//
///**
// * @author hankun
// */
//@Slf4j
//public class DataSourceLoader {
//
//    private final DataSourceCreator dataSourceCreator;
//
//    private final DsPropertiesProvider dsPropertiesProvider;
//
//    private final ConnectionConfigBuilder connectionConfigBuilder;
//
//    private final DsConfigReader dsConfigReader;
//
//    public DataSourceLoader(DataSourceCreator dataSourceCreator,
//                            DsPropertiesProvider dsPropertiesProvider,
//                            ConnectionConfigBuilder connectionConfigBuilder,
//                            DsConfigReader dsConfigReader) {
//        this.dataSourceCreator = dataSourceCreator;
//        this.dsPropertiesProvider = dsPropertiesProvider;
//        this.connectionConfigBuilder = connectionConfigBuilder;
//        this.dsConfigReader = dsConfigReader;
//    }
//
//    public boolean groupExists(String group) {
//        return dsPropertiesProvider.listGroup().contains(group);
//    }
//
//    public DataSource load(String ds, String domain, BuildInfoHolder buildInfoHolder) {
//        DataSourceConfig dataSourceConfig = dsPropertiesProvider.getDataSourceConfig(ds);
//        if (dataSourceConfig == null) {
//            buildInfoHolder.addMessage("数据源别名非法，请先通过配置注册别名：" + ds);
//            return null;
//        }
//        updateConfig(dataSourceConfig, domain);
//        ConnectionConfig config = connectionConfigBuilder.build(dataSourceConfig, buildInfoHolder);
//        if (config == null) {
//            return null;
//        }
//        DataSource dataSource = dataSourceCreator.create(dataSourceConfig, config);
//        if (dataSource == null) {
//            buildInfoHolder.addMessage("数据源创建失败，" + dataSourceConfig);
//        }
//        return dataSource;
//    }
//
//    private void updateConfig(DataSourceConfig dataSourceConfig, String domain) {
//        dataSourceConfig.setServiceName(dsConfigReader.getCurrentService());
//        dataSourceConfig.setDomain(domain);
//    }
//}
