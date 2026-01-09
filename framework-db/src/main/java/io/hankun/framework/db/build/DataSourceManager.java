//package io.hankun.framework.db.build;
//
//import com.baomidou.dynamic.datasource.ds.GroupDataSource;
//
//import javax.sql.DataSource;
//
///**
// * @author hankun
// */
//public class DataSourceManager {
//
//    private final DynamicRoutingDataSource dynamicRoutingDataSource;
//
//    private final DsPropertiesProvider propertiesProvider;
//
//    public DataSourceManager(DynamicRoutingDataSource dynamicRoutingDataSource,
//                             DsPropertiesProvider propertiesProvider) {
//        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
//        this.propertiesProvider = propertiesProvider;
//    }
//
//    public DataSource getDataSource(String ds, String domain) {
//        return dynamicRoutingDataSource.loadDataSource(ds, domain);
//    }
//
//    public void loadDomain(String domain) {
//        for (String ds : propertiesProvider.listGroup()) {
//            dynamicRoutingDataSource.loadDataSource(ds, domain);
//        }
//    }
//
//
//    public DataSource getAnyOf(String ds) {
//        GroupDataSource groupDataSource = dynamicRoutingDataSource.getGroupDataSources().get(ds);
//        if (groupDataSource != null) {
//            return groupDataSource.getDataSourceMap().values().stream().findFirst().orElse(null);
//        }
//        return null;
//    }
//}
