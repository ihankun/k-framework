//package io.ihankun.framework.db.dynamic.aspect;
//
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
//import io.ihankun.framework.db.auth.AuthCheckService;
//import io.ihankun.framework.db.config.DataSourceConstant;
//import io.ihankun.framework.db.dynamic.DataSourceCacheCreator;
//import io.ihankun.framework.db.dynamic.DbCreateErrorContext;
//import io.ihankun.framework.db.dynamic.PropertiesHolder;
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.core.env.Environment;
//import org.springframework.core.env.PropertySource;
//import org.springframework.core.env.StandardEnvironment;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author hankun
// */
//@Aspect
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "true")
//@Component
//@Slf4j
//public class PropertyAspect {
//
//    @Resource
//    Environment environment;
//
//    @Resource
//    PropertiesHolder holder;
//
//    @Resource
//    private AuthCheckService authCheckService;
//
//    private String[] hospitals;
//
//    private static final String DEFAULT_DS_SUFFIX = "kun.ds.hospital.default";
//    private static final String DEFAULT_HOSPITAL = "defaultHospital";
//    private static final String SCAN_DS_SUFFIX = "kun.ds.hospital.scan";
//    private static final String DS_SPLIT = "_";
//
//    private static final String DS_CONFIG = "kun.datasource.config.properties";
//
//    /**
//     * postgresql
//     */
//    private static final String DS_URL_SUFFIX = "kun.datasource.url.suffix";
//    private static final String URL_SUFFIX = "useUnicode=true&characterEncoding=utf8&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai";
//
//    /**
//     * clickhouse
//     */
//    private static final String CH_DS_URL_SUFFIX = "kun.ch.datasource.url.suffix";
//    private static final String CH_URL_SUFFIX = "";
//
//    /**
//     * 数据源前缀对应驱动定义
//     */
//    private static final Map<String, DataSourceProperties> datasourcePropertiesMap = new HashMap<>(3);
//    static {
//        datasourcePropertiesMap.put("kun.ds.datasource.", new DataSourceProperties("org.postgresql.Driver", "jdbc:postgresql://", DS_URL_SUFFIX, URL_SUFFIX));
//        datasourcePropertiesMap.put("kun.ch.ds.datasource.", new DataSourceProperties("com.clickhouse.jdbc.ClickHouseDriver", "jdbc:clickhouse://", CH_DS_URL_SUFFIX, CH_URL_SUFFIX));
//    }
//
//    @Pointcut("execution(public * com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties.getDatasource())")
//    public void loadPoint() {
//
//    }
//
//    /**
//     * 修改系统加载的配置信息
//     *
//     * @param result 系统加载的配置信息
//     */
//    @AfterReturning(returning = "result", value = "loadPoint()")
//    public void afterGet(Map<String, DataSourceProperty> result) {
//        boolean isPro = Boolean.parseBoolean(environment.getProperty(DS_CONFIG));
//        //取默认后缀
//        String defaultSuffix = environment.getProperty(DEFAULT_DS_SUFFIX);
//        if (StringUtils.isEmpty(defaultSuffix)) {
//            defaultSuffix = DEFAULT_HOSPITAL;
//        }
//        String domains = environment.getProperty(SCAN_DS_SUFFIX);
//        if (StringUtils.isNotBlank(domains)) {
//            hospitals = domains.split(DataSourceConstant.COMMA_SPLIT);
//        } else {
//            hospitals = new String[]{};
//        }
//        if ((!checkSource(result)) || isPro) {
//            log.warn("PropertyAspect.afterGet.use.bootstrap,load config info={},force={}", result.entrySet(), isPro);
//            Map<String, DataSourceProperty> propertyMap = getProperties(defaultSuffix);
//            if (CollectionUtils.isEmpty(propertyMap)) {
//                log.error("PropertyAspect.afterGet.getProperties.no.find.any.config!");
//            }
//            holder.setPropertyMap(propertyMap);
//            result.clear();
//            for (Map.Entry<String, DataSourceProperty> entry : propertyMap.entrySet()) {
//                if (!checkDsConfigPropertiesIsExist(entry)) {
//                    continue;
//                }
//                String key = updateSource(defaultSuffix, entry.getKey(), entry.getValue());
//                result.put(key, entry.getValue());
//            }
//            return;
//        }
//        log.warn("PropertyAspect.afterGet.use.config.files,load config count={},defDomain={}", result.size(), defaultSuffix);
//        holder.setPropertyMap(result);
//        Map<String, DataSourceProperty> data = new HashMap<>(result.size());
//        for (Map.Entry<String, DataSourceProperty> entry : result.entrySet()) {
//            if (!entry.getKey().contains(DS_SPLIT)) {
//                String key = updateSource(defaultSuffix, entry.getKey() + DS_SPLIT + defaultSuffix, entry.getValue());
//                data.put(key, entry.getValue());
//            } else {
//                String suffix = entry.getKey().split(DS_SPLIT)[1];
//                String key = updateSource(suffix, entry.getKey(), entry.getValue());
//                data.put(key, entry.getValue());
//            }
//        }
//        result.clear();
//        result.putAll(data);
//    }
//
//    private boolean checkSource(Map<String, DataSourceProperty> result) {
//        if (CollectionUtils.isEmpty(result)) {
//            return false;
//        }
//        String master = environment.getProperty(DataSourceConstant.DS_PRIMARY);
//        if (master == null) {
//            master = DataSourceConstant.MASTER;
//        }
//        for (String key : result.keySet()) {
//            String alias = key.split(DS_SPLIT)[0];
//            if (master.equals(alias)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private String updateSource(String domain, String key, DataSourceProperty property) {
//        String alias = key.split(DS_SPLIT)[0];
//        DbCreateErrorContext.init();
//        try {
//            //更新数据源信息
//            if (DataSourceCacheCreator.buildDataSource(property, environment, domain, alias)) {
//                String db = DataSourceCacheCreator.getDb(property.getUrl());
//                log.info("PropertyAspect.afterGet.updateSource.update.success,dataSource={},updateTo={},db={},usedName={}",
//                        key, alias + DS_SPLIT + domain, db, property.getUsername());
//                return alias + DS_SPLIT + domain;
//            } else {
//                for (String hospital : hospitals) {
//                    log.info("PropertyAspect.afterGet.updateSource.update.retry,domain={},hospitals={}", hospital, hospitals);
//                    if (DataSourceCacheCreator.buildDataSource(property, environment, hospital, alias)) {
//                        String db = DataSourceCacheCreator.getDb(property.getUrl());
//                        log.info("PropertyAspect.afterGet.updateSource.update.success,dataSource={},updateTo={},db={},usedName={}",
//                                key, alias + DS_SPLIT + hospital, db, property.getUsername());
//                        authCheckService.validate(db, property.getUsername());
//                        return alias + DS_SPLIT + hospital;
//                    }
//                }
//                String db = DataSourceCacheCreator.getDb(property.getUrl());
//                String addr = DataSourceCacheCreator.getAddr(property.getUrl());
//                log.info("PropertyAspect.afterGet.updateSource.do.not.change,dataSource={},dbName={},dbUrl={}", key, db, addr);
//            }
//            boolean needCheck = !Boolean.parseBoolean(environment.getProperty("kun.datasource.disable.check.connect"));
//            if (needCheck && !DataSourceCacheCreator.checkDataSource(property)) {
//                throw new DbException("数据源无法连接，数据源名称:" + alias + DS_SPLIT + domain + ",error:" + DbCreateErrorContext.get());
//            }
//
//            return alias + DS_SPLIT + domain;
//        } finally {
//            DbCreateErrorContext.clear();
//        }
//    }
//
//    /**
//     * @return 获取数据源配置信息
//     */
//    private Map<String, DataSourceProperty> getProperties(String def) {
//        Map<String, DataSourceProperty> propertyMap = new HashMap<>(4);
//        if (environment instanceof StandardEnvironment) {
//            StandardEnvironment standardEnvironment = (StandardEnvironment) environment;
//            for (PropertySource source : standardEnvironment.getPropertySources()) {
//                Object o = source.getSource();
//                if (o instanceof Map) {
//                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
//                        String key = entry.getKey();
//                        String data = entry.getValue().toString();
//                        DataSourceProperties properties = getDataSourceProperties(key);
//                        if (properties != null) {
//                            DataSourceProperty property = parseDataSourceProperty(key, data, properties);
//                            if (property != null) {
//                                propertyMap.put(properties.getAlias() + "_" + def, property);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return propertyMap;
//    }
//
//    private DataSourceProperty parseDataSourceProperty(String key, String data, DataSourceProperties dataSourceProperties) {
//        int pointA = data.indexOf(DataSourceConstant.DS_DATA_SPLIT);
//        int pointB = data.indexOf(DataSourceConstant.DS_POINT);
//        if (pointA < 0 || pointB < 0) {
//            log.error("PropertyAspect.afterGet.parseDataSourceProperty.wrong.config,info={},value={}", key, data);
//            return null;
//        }
//
//        String addr = data.substring(0, pointA);
//        String db = data.substring(pointA + 1, pointB);
//        String user = data.substring(pointB + 1);
//        if (StringUtils.isEmpty(addr) || StringUtils.isEmpty(db) || StringUtils.isEmpty(user)) {
//            log.error("PropertyAspect.afterGet.parseDataSourceProperty.config.error,info={},value={}", key, data);
//            return null;
//        }
//
//        authCheckService.validate(db, user);
//
//        String urlSuffix = environment.getProperty(dataSourceProperties.getUrlSuffix(), dataSourceProperties.getDefaultSuffix());
//        if (StringUtils.isNotBlank(urlSuffix)) {
//            urlSuffix = DataSourceConstant.DS_QUESTION +  urlSuffix;
//        }
//
//        DataSourceProperty property = new DataSourceProperty();
//        property.setUsername(user);
//        property.setDriverClassName(dataSourceProperties.getDriverClassName());
//        property.setUrl(dataSourceProperties.getUrlPrefix() + addr + "/" + db + urlSuffix);
//        return property;
//    }
//
//    private DataSourceProperties getDataSourceProperties(String key) {
//        return datasourcePropertiesMap.entrySet()
//                .stream()
//                .filter(entry -> key.startsWith(entry.getKey()))
//                .map(entry -> {
//                    DataSourceProperties properties = entry.getValue();
//                    String alias = key.substring(entry.getKey().length());
//                    properties.setAlias(alias);
//                    return properties;
//                })
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * 检测新配置方式的数据源是否存在
//     *
//     * @param entry
//     * @return
//     */
//    private boolean checkDsConfigPropertiesIsExist(Map.Entry<String, DataSourceProperty> entry) {
//        try {
//            String domain = entry.getKey().split(DS_SPLIT)[1];
//            String jdbcUrl = entry.getValue().getUrl();
//            int startIndex = jdbcUrl.indexOf("://") + 3;
//            int endIndex = jdbcUrl.indexOf("/", startIndex);
//            String addr = jdbcUrl.substring(startIndex, endIndex);
//            if (!environment.containsProperty(DataSourceConstant.DS_PREFIX + domain + DataSourceConstant.DS_POINT + addr)) {
//                // 备用数据源是否存在
//                for (String hospital : hospitals) {
//                    if (environment.containsProperty(DataSourceConstant.DS_PREFIX + hospital + DataSourceConstant.DS_POINT + addr)) {
//                        log.info("PropertyAspect.afterGet.checkDsConfigPropertiesIsExist.scan.ds.true domain={}", hospital);
//                        return true;
//                    }
//                }
//                log.error("数据源无法连接，连接信息不存在，数据源名称:" + addr + DS_SPLIT + domain + ",error:" + DbCreateErrorContext.get());
//                return false;
//            }
//        } catch (Exception e) {
//            log.error("PropertyAspect.afterGet.checkDsConfigPropertiesIsExist.error entry={}", entry, e);
//        }
//        return true;
//    }
//
//    @Data
//    private static class DataSourceProperties {
//        private final String driverClassName;
//        /**
//         * 别名
//         */
//        private String alias;
//        /**
//         * 前缀
//         */
//        private final String urlPrefix;
//        /**
//         * 后缀
//         */
//        private final String urlSuffix;
//        /**
//         * 后缀默认值
//         */
//        private final String defaultSuffix;
//
//        public DataSourceProperties(String driverClassName, String urlPrefix, String urlSuffix, String defaultSuffix) {
//            this.driverClassName = driverClassName;
//            this.urlPrefix = urlPrefix;
//            this.urlSuffix = urlSuffix;
//            this.defaultSuffix = defaultSuffix;
//        }
//    }
//}
