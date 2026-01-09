//package io.hankun.framework.db.build;
//
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
//import io.hankun.framework.db.build.config.DsConfigReader;
//import io.hankun.framework.db.build.connect.ConnectionConfigBuilder;
//import io.hankun.framework.db.build.connect.entity.ConnectionConfig;
//import io.hankun.framework.db.build.druid.DruidConfigBuilder;
//import io.hankun.framework.db.build.ds.DataSourceConfig;
//import io.hankun.framework.db.build.util.DbUrlHelper;
//import io.hankun.framework.db.build.util.PropertyHelper;
//import io.hankun.framework.db.config.DataSourceConstant;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author hankun
// */
//@Slf4j
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "false", matchIfMissing = true)
//@Component
//public class KDynamicDataSourcePropertiesCustomizer implements DynamicDataSourcePropertiesCustomizer {
//
//
//    public static final int SUFFIX_INDEX = 1;
//    private final ConnectionConfigBuilder connectionConfigBuilder;
//
//
//    private final DsPropertiesProvider dsPropertiesProvider;
//
//
//    private final DsConfigReader kunDsConfigReader;
//
//    private final DruidConfigBuilder druidConfigBuilder;
//
//    public KDynamicDataSourcePropertiesCustomizer(ConnectionConfigBuilder connectionConfigBuilder,
//                                                  DsPropertiesProvider dsPropertiesProvider,
//                                                  DsConfigReader kunDsConfigReader,
//                                                  DruidConfigBuilder druidConfigBuilder) {
//        this.connectionConfigBuilder = connectionConfigBuilder;
//        this.dsPropertiesProvider = dsPropertiesProvider;
//        this.kunDsConfigReader = kunDsConfigReader;
//        this.druidConfigBuilder = druidConfigBuilder;
//    }
//
//    @Override
//    public void customize(DynamicDataSourceProperties properties) {
//        if (!kunDsConfigReader.getLoadOldConfig()) {
//            properties.getDatasource().clear();
//            log.warn("忽略苞米豆配置，count={}", properties.getDatasource().size());
//            return;
//        }
//        Map<String, DataSourceProperty> result = new HashMap<>(properties.getDatasource().size());
//        List<String> allDomain = kunDsConfigReader.getAllDomain();
//        for (Map.Entry<String, DataSourceProperty> entry : properties.getDatasource().entrySet()) {
//            String[] split = entry.getKey().split(DataSourceConstant.DS_SPLIT);
//            String ds = split[0];
//            String suffix = split.length > SUFFIX_INDEX ? split[SUFFIX_INDEX] : getSuffix();
//            boolean suc = false;
//            for (String domain : allDomain) {
//                DataSourceProperty property = build(ds, domain, entry.getValue());
//                if (property != null) {
//                    result.put(ds + DataSourceConstant.DS_SPLIT + domain, property);
//                    log.info("转换苞米豆配置成功，ds={},property={}", ds + DataSourceConstant.DS_SPLIT + domain,
//                            PropertyHelper.buildOutput(property));
//                    suc = true;
//                    break;
//                }
//            }
//            if (!suc) {
//                result.put(ds + DataSourceConstant.DS_SPLIT + suffix, entry.getValue());
//                log.warn("转换苞米豆配置失败，不进行修改，ds={},property={}", ds + DataSourceConstant.DS_SPLIT + suffix,
//                        PropertyHelper.buildOutput(entry.getValue()));
//            }
//        }
//        properties.getDatasource().clear();
//        properties.getDatasource().putAll(result);
//    }
//
//    private String getSuffix() {
//        String defDomain = kunDsConfigReader.getDefDomain();
//        if (StringUtils.isEmpty(defDomain)) {
//            return DsConfigReader.DEFAULT;
//        }
//        return defDomain;
//    }
//
//    private DataSourceProperty build(String ds, String domain, DataSourceProperty dataSourceProperty) {
//        DataSourceConfig dataSourceConfig;
//        dataSourceConfig = new DataSourceConfig();
//        dataSourceConfig.setDbMark(DbUrlHelper.getAddress(dataSourceProperty.getUrl()));
//        dataSourceConfig.setDs(ds);
//        dataSourceConfig.setDb(DbUrlHelper.getDb(dataSourceProperty.getUrl()));
//        dataSourceConfig.setUser(dataSourceProperty.getUsername());
//        dataSourceConfig.setSchema("");
//        dataSourceConfig.setProtocol(DbUrlHelper.getProtocol(dataSourceProperty.getUrl()));
//        dataSourceConfig.setParameter(DbUrlHelper.getParameter(dataSourceProperty.getUrl()));
//        dataSourceConfig.setDriver(dataSourceProperty.getDriverClassName());
//
//        dataSourceConfig.setServiceName(kunDsConfigReader.getCurrentService());
//        dataSourceConfig.setDomain(domain);
//        dataSourceConfig.setSeata(dataSourceProperty.getSeata());
//
//        BuildInfoHolder buildInfoHolder = new BuildInfoHolder();
//        ConnectionConfig config = connectionConfigBuilder.build(dataSourceConfig, buildInfoHolder);
//        if (config == null) {
//            log.error("数据源连接信息构建失败,{},config={}", buildInfoHolder.getErrorMessage(ds, domain), dataSourceConfig);
//            return null;
//        }
//        dsPropertiesProvider.addDsConfig(dataSourceConfig);
//        return PropertyHelper.buildProperty(config, ds + DataSourceConstant.DS_SPLIT + domain,
//                druidConfigBuilder.createDruidConfig(dataSourceConfig), dataSourceProperty.getSeata());
//    }
//}
