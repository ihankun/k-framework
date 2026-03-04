//package io.hankun.framework.db.build;
//
//import io.hankun.framework.db.build.config.ChConfig;
//import io.hankun.framework.db.build.config.DsConfig;
//import io.hankun.framework.db.build.config.DsConfigReader;
//import io.hankun.framework.db.build.connect.ConfigOverriderFilter;
//import io.hankun.framework.db.build.connect.ConnectionConfigBuilder;
//import io.hankun.framework.db.build.connect.config.DsOverrideConfig;
//import io.hankun.framework.db.build.druid.DruidConfigBuilder;
//import io.hankun.framework.db.build.druid.config.DsDruidConfig;
//import io.hankun.framework.db.build.ds.DataSourceCreator;
//import io.hankun.framework.db.build.ds.DataSourceLoader;
//import io.hankun.framework.db.build.util.ConnectTester;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//import java.util.List;
//
///**
// * @author hankun
// */
//@Slf4j
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "false", matchIfMissing = true)
//@Configuration
//public class DsAutoConfiguration {
//
//    private final DsOverrideConfig dsOverrideConfig;
//
//    private final DsDruidConfig dsDruidConfig;
//
//    private final DsConfig dsConfig;
//
//    private final ChConfig chConfig;
//
//    public DsAutoConfiguration(DsOverrideConfig dsOverrideConfig,
//                               DsDruidConfig dsDruidConfig,
//                               DsConfig dsConfig,
//                               ChConfig chConfig) {
//        this.dsOverrideConfig = dsOverrideConfig;
//        this.dsDruidConfig = dsDruidConfig;
//        this.dsConfig = dsConfig;
//        this.chConfig = chConfig;
//    }
//
//    @Bean
//    public DsConfigReader dsConfigReader(Environment environment) {
//        return new DsConfigReader(dsConfig, environment);
//    }
//
//    @Bean
//    public DsPropertiesProvider kunDsPropertiesProvider(DsConfigReader kunDsPropertiesProvider) {
//        return new DsPropertiesProvider(dsConfig, chConfig, kunDsPropertiesProvider);
//    }
//
//    @Bean
//    public ConnectionConfigBuilder connectionConfigBuilder(List<ConfigOverriderFilter> configOverriderFilters,
//                                                           DsConfigReader kunDsConfigReader) {
//        return new ConnectionConfigBuilder(configOverriderFilters, kunDsConfigReader,
//                dsOverrideConfig, new ConnectTester());
//    }
//
//    @Bean
//    public DruidConfigBuilder druidConfigBuilder() {
//        return new DruidConfigBuilder(dsDruidConfig, dsConfig);
//    }
//
//    @Bean
//    public DataSourceLoader dataSourceLoader(DataSourceCreator dataSourceCreator, DsPropertiesProvider dsPropertiesProvider,
//                                             ConnectionConfigBuilder connectionConfigBuilder, DsConfigReader kunDsConfigReader) {
//        return new DataSourceLoader(dataSourceCreator, dsPropertiesProvider,
//                connectionConfigBuilder, kunDsConfigReader);
//    }
//
//    @Bean
//    public DataSourceManager dataSourceManager(DynamicRoutingDataSource dataSource,
//                                               DsPropertiesProvider dsPropertiesProvider) {
//        return new DataSourceManager(dataSource, dsPropertiesProvider);
//    }
//
//}
