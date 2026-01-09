//package io.hankun.framework.db.dynamic.aspect;
//
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import com.baomidou.dynamic.datasource.ds.GroupDataSource;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
//import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
//import io.hankun.framework.core.context.DomainContext;
//import io.hankun.framework.core.utils.spring.SpringHelpers;
//import io.hankun.framework.db.dynamic.DataSourceCacheCreator;
//import io.hankun.framework.db.dynamic.DbCreateErrorContext;
//import io.hankun.framework.db.dynamic.DomainDynamicDataSourceStrategy;
//import io.hankun.framework.db.dynamic.PropertiesHolder;
//import io.hankun.framework.db.exceptions.DbException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * @author hankun
// */
//@Aspect
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "true")
//@Component
//@Slf4j
//public class DataSourcePropertyAspect {
//
//    @Resource
//    private PropertiesHolder propertiesHolder;
//
//    @Pointcut("execution(* com.baomidou.dynamic.datasource.ds.AbstractRoutingDataSource.getConnection(..)))")
//    public void getDataSourcePointcut() {
//    }
//
//    @Before("getDataSourcePointcut()")
//    public void beforeGetDataSource(JoinPoint joinPoint) {
//        String domain = DomainContext.get();
//        if (StringUtils.isBlank(domain)) {
//            return;
//        }
//
//        String dbMark = DynamicDataSourceContextHolder.peek();
//        dbMark = StringUtils.isBlank(dbMark) ? "master" : dbMark;
//
//        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) joinPoint.getTarget();
//        Map<String, GroupDataSource> currentGroupDataSources = dynamicRoutingDataSource.getGroupDataSources();
//        if (currentGroupDataSources.containsKey(dbMark) && !currentGroupDataSources.get(dbMark).getDataSourceMap().isEmpty()) {
//            if (log.isDebugEnabled()) {
//                log.debug("DataSourcePropertyAspect.beforeGetDataSource.getCurrentGroupDataSources.contains.{} domain={}", dbMark, dbMark);
//            }
//            return;
//        }
//
//        DataSourceProperty dataSourceProperty = propertiesHolder.getModel(dbMark);
//        if (Objects.isNull(dataSourceProperty)) {
//            if (log.isDebugEnabled()) {
//                log.debug("DataSourcePropertyAspect.beforeGetDataSource.propertiesHolder.getModel.is.null.{} domain={}", dbMark, dbMark);
//            }
//            return;
//        }
//
//        ApplicationContext context = SpringHelpers.context();
//        try {
//            String reRouteDomain = DomainDynamicDataSourceStrategy.getReRouteDomain(domain);
//            DataSourceCacheCreator creator = context.getBean(DataSourceCacheCreator.class);
//            DbCreateErrorContext.init();
//            DataSource source = creator.createDataSource(dbMark, reRouteDomain);
//            if (source == null) {
//                log.error("DataSourcePropertyAspect.beforeGetDataSource.fail.create.ds.by.dataSource,domain={},ds={}", reRouteDomain, dbMark);
//                throw new DbException("无法找到匹配的数据源" + ",thread:" + Thread.currentThread().getName() + ",域名:" + reRouteDomain + ",别名:" + dbMark + "错误信息:" + DbCreateErrorContext.get());
//            }
//        } finally {
//            DbCreateErrorContext.clear();
//        }
//    }
//}
