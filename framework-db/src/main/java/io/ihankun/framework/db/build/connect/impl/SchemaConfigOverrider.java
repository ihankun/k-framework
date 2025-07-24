//package io.ihankun.framework.db.build.connect.impl;
//
//import io.ihankun.framework.db.build.BuildInfoHolder;
//import io.ihankun.framework.db.build.connect.ConfigOverriderFilter;
//import io.ihankun.framework.db.build.connect.entity.ConnectionConfig;
//import io.ihankun.framework.db.build.ds.DataSourceConfig;
//import io.ihankun.framework.db.config.DataSourceConstant;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class SchemaConfigOverrider implements ConfigOverriderFilter {
//
//    @Override
//    public String name() {
//        return "schema";
//    }
//
//    @Override
//    public int order() {
//        return 0;
//    }
//
//    @Override
//    public void before(DataSourceConfig dataSourceConfig, BuildInfoHolder buildInfoHolder) {
//
//    }
//
//    @Override
//    public void after(DataSourceConfig dataSourceConfig, ConnectionConfig connectionConfig, BuildInfoHolder buildInfoHolder, Connection connection) throws SQLException {
//        //包含schema，无需添加
//        String url = connectionConfig.getUrl();
//        if (url.contains(DataSourceConstant.DS_SCHEMA)) {
//            log.debug("DataSourceCacheCreator.dataSource.schema.exists.in.url,url={}", url);
//            return;
//        }
//        String currentSchema = connection.getSchema();
//        if (StringUtils.isEmpty(currentSchema)) {
//            log.error("DataSourceCacheCreator.dataSource.schema.not.exists,url={}", url);
//            return;
//        }
//        //添加schema信息
//        int point = url.indexOf(DataSourceConstant.DS_QUESTION);
//        if (point > 0) {
//            url = url.substring(0, point + 1) + DataSourceConstant.DS_SCHEMA + currentSchema + DataSourceConstant.DS_SPLIT_OF_PARAMETER + url.substring(point + 1);
//            log.debug("DataSourceCacheCreator.dataSource.add.schema.to.url,schema={},url={}", currentSchema, url);
//        }else {
//            url += DataSourceConstant.DS_QUESTION + DataSourceConstant.DS_SCHEMA + currentSchema;
//        }
//        connectionConfig.setUrl(url);
//    }
//}
