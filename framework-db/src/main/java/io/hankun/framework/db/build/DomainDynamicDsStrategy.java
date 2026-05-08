//package io.hankun.framework.db.build;
//
//import com.baomidou.dynamic.datasource.strategy.DynamicDataSourceStrategy;
//import io.hankun.framework.core.context.sys.DomainContext;
//import io.hankun.framework.db.build.config.DsConfigReader;
//import io.hankun.framework.db.build.ds.DsContext;
//import io.hankun.framework.db.config.DataSourceConstant;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.StringUtils;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @author hankun
// */
//@Slf4j
//@Setter
//public class DomainDynamicDsStrategy implements DynamicDataSourceStrategy {
//
//    /**
//     * 忽略数据源匹配的线程
//     */
//    private static final String FIRST_THREAD_NAME = "main,RMI TCP Connection";
//
//    /**
//     * 分隔字符串
//     */
//    private static final String SPLIT = ",";
//
//
//    private DsConfigReader dsConfigReader;
//
//    @Override
//    public String determineKey(List<String> dsNames) {
//        String ds = DsContext.getDataSourceName();
//        String domain = getDomain(ds);
//        if (StringUtils.isEmpty(domain)) {
//            return "";
//        }
//        return ds + DataSourceConstant.DS_SPLIT + domain;
//    }
//
//    public String getDomain(String ds) {
//        String currentDomain = DomainContext.get();
//        //域名不存在
//        if (StringUtils.isEmpty(currentDomain)) {
//            //线程匹配
//            if (defaultThreadMatch(Thread.currentThread().getName())) {
//                //获取默认域名
//                return DynamicRoutingDataSource.RANDOM_DOMAIN;
//            } else {
//                //线程不匹配，且无域名上下文
//                log.error("域名上下文为空，且非main线程，ds={}", ds);
//                throw new RuntimeException("域名上下文为空，且非main线程，ds=" + ds);
//            }
//        }
//        //域名重定向
//        return dsConfigReader.getReRouteDomain(currentDomain);
//    }
//
//    /**
//     * 默认现成匹配逻辑
//     *
//     * @param threadName 线程名称
//     * @return 是否访问默认域名
//     */
//    private boolean defaultThreadMatch(String threadName) {
//        return Arrays.stream(FIRST_THREAD_NAME.split(SPLIT)).anyMatch(threadName::startsWith);
//    }
//
//}
