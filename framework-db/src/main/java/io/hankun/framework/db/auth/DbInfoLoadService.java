//package io.hankun.framework.db.auth;
//
//import io.hankun.framework.core.invocation.DbInfoLoader;
//import io.hankun.framework.core.invocation.entity.DsInfo;
//import io.hankun.framework.db.build.DataSourceManager;
//import io.hankun.framework.db.build.DsPropertiesProvider;
//import io.hankun.framework.db.build.ds.DataSourceConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//@ConditionalOnProperty(prefix = "kun.invocation.config.auth", value = "enabled", havingValue = "true")
//public class DbInfoLoadService implements DbInfoLoader {
//
//    @Autowired
//    private DsPropertiesProvider dsPropertiesProvider;
//
//    @Autowired
//    private DataSourceManager dataSourceManager;
//
//    @Override
//    public List<DsInfo> loadDbInfoList() {
//        try {
//            List<DsInfo> dsInfoList = new ArrayList<>();
//            for (DataSourceConfig config : dsPropertiesProvider.listGroupConfig()) {
//
//                String db = config.getDb();
//                String schema = config.getSchema();
//                if (StringUtils.isEmpty(schema)) {
//                    schema = getSchemaFromDs(config);
//                }
//                DsInfo dsInfo = new DsInfo();
//                dsInfo.setDb(db);
//                dsInfo.setSchema(schema);
//                dsInfo.setUserName(config.getUser());
//                dsInfo.setDsName(config.getDs());
//                dsInfo.setDsMark(config.getDbMark());
//                dsInfoList.add(dsInfo);
//            }
//            return dsInfoList;
//        } catch (Exception e) {
//            log.error("获取数据库信息失败", e);
//            return new ArrayList<>();
//        }
//    }
//
//    private String getSchemaFromDs(DataSourceConfig config) {
//        DataSource dataSource = dataSourceManager.getAnyOf(config.getDs());
//        if (dataSource != null) {
//            try {
//                return ServiceTableChecker.getSchema(dataSource.getConnection().getMetaData().getURL());
//            } catch (SQLException e) {
//                log.error("获取数据库schema失败,config={}", config, e);
//                return "";
//            }
//        }
//        return "";
//    }
//}
