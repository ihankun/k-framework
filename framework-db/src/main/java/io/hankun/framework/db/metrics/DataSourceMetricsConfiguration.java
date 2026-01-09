//package io.hankun.framework.db.metrics;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import com.baomidou.dynamic.datasource.ds.ItemDataSource;
//import io.hankun.framework.db.dynamic.DataSourceBuildEvent;
//import io.hankun.framework.db.dynamic.DataSourceRemoveEvent;
//import io.micrometer.core.instrument.MeterRegistry;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//
//import javax.sql.DataSource;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
///**
// * @author hankun
// */
//@Configuration
//@ConditionalOnClass({DynamicRoutingDataSource.class, DruidDataSource.class, MeterRegistry.class})
//@Slf4j
//@AllArgsConstructor
//@EnableAsync
//public class DataSourceMetricsConfiguration {
//
//    private final MeterRegistry registry;
//
//    @Autowired
//    public void bindMetricsRegistryToDruidDataSources(Collection<DataSource> dataSources, DataSourceCollector dataSourceCollector) {
//        try {
//            List<DynamicRoutingDataSource> dynamicRoutingDataSources = new ArrayList<>(dataSources.size());
//            for (DataSource dataSource : dataSources) {
//                DynamicRoutingDataSource druidDataSource = dataSource.unwrap(DynamicRoutingDataSource.class);
//                if (druidDataSource != null) {
//                    dynamicRoutingDataSources.add(druidDataSource);
//                }
//            }
//            dataSourceCollector.register(dynamicRoutingDataSources);
//            log.info("finish register datasource metrics to micrometer");
//        } catch (Exception e) {
//            log.error("error register datasource metrics to micrometer!!!", e);
//        }
//    }
//
//    @Bean
//    public DataSourceCollector dataSourceCollector() {
//        return new DataSourceCollector(registry);
//    }
//
//    @EventListener
//    @Async
//    public void listen(DataSourceBuildEvent event) {
//        try {
//            DataSource dataSource = event.getDataSource();
//            if (dataSource instanceof ItemDataSource) {
//                ItemDataSource itemDataSource = (ItemDataSource) dataSource;
//                DataSource realDataSource = itemDataSource.getRealDataSource();
//                if (realDataSource instanceof DruidDataSource) {
//                    dataSourceCollector().datasourceRegister((DruidDataSource) realDataSource);
//                    log.info("update register datasource metrics to micrometer");
//                    return;
//                }
//            }
//            log.warn("datasource type not DruidDataSource, miss current datasource metrics");
//        } catch (Exception e) {
//            log.info("error update register datasource metrics to micrometer", e);
//        }
//    }
//
//    @EventListener
//    @Async
//    public void listenRemove(DataSourceRemoveEvent event) {
//        try {
//            DataSource dataSource = event.getDataSource();
//            if (dataSource instanceof ItemDataSource) {
//                ItemDataSource itemDataSource = (ItemDataSource) dataSource;
//                DataSource realDataSource = itemDataSource.getRealDataSource();
//                if (realDataSource instanceof DruidDataSource) {
//                    dataSourceCollector().remove(((DruidDataSource) realDataSource).getName());
//                    log.info("remove register datasource metrics to micrometer");
//                    return;
//                }
//            }
//            log.warn("datasource type not DruidDataSource, miss current datasource metrics");
//        } catch (Exception e) {
//            log.info("error remove register datasource metrics to micrometer", e);
//        }
//    }
//}
