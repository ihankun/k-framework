//package io.ihankun.framework.db.build.connect.impl;
//
//
//import io.ihankun.framework.db.build.BuildInfoHolder;
//import io.ihankun.framework.db.build.config.DsConfigReader;
//import io.ihankun.framework.db.build.connect.ConfigOverriderFilter;
//import io.ihankun.framework.db.build.connect.entity.ConnectionConfig;
//import io.ihankun.framework.db.build.ds.DataSourceConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class ApplicationNameOverrider implements ConfigOverriderFilter {
//
//    public static final String APPLICATION_NAME = "ApplicationName";
//
//    private final DsConfigReader dsConfigReader;
//
//    public ApplicationNameOverrider(DsConfigReader dsConfigReader) {
//        this.dsConfigReader = dsConfigReader;
//    }
//
//    @Override
//    public String name() {
//        return APPLICATION_NAME;
//    }
//
//    @Override
//    public int order() {
//        return 100;
//    }
//
//    @Override
//    public void before(DataSourceConfig dataSourceConfig, BuildInfoHolder buildInfoHolder) {
//        String parameter = dataSourceConfig.getParameter();
//        String applicationName = dsConfigReader.getCurrentService();
//        if (StringUtils.hasText(parameter) && parameter.contains(APPLICATION_NAME)) {
//            String[] params = parameter.split("&");
//            for (int i = 0; i < params.length; i++) {
//                if (params[i].startsWith(APPLICATION_NAME)) {
//                    params[i] = APPLICATION_NAME + "=" + applicationName;
//                }
//            }
//            dataSourceConfig.setParameter(String.join("&", params));
//        } else if (StringUtils.hasText(parameter)) {
//            dataSourceConfig.setParameter(dataSourceConfig.getParameter() + "&" + APPLICATION_NAME + "=" + applicationName);
//        } else {
//            dataSourceConfig.setParameter(APPLICATION_NAME + "=" + applicationName);
//        }
//    }
//
//    @Override
//    public void after(DataSourceConfig dataSourceConfig, ConnectionConfig connectionConfig, BuildInfoHolder buildInfoHolder, Connection connection) throws SQLException {
//
//    }
//}
