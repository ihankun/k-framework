//package io.hankun.framework.db.dynamic;
//
//import com.baomidou.dynamic.datasource.ds.ItemDataSource;
//import com.baomidou.dynamic.datasource.strategy.DynamicDataSourceStrategy;
//import io.hankun.framework.core.context.sys.DomainContext;
//import io.hankun.framework.core.context.login.LoginUserContext;
//import io.hankun.framework.core.context.login.LoginUserInfo;
//import io.hankun.framework.core.utils.spring.SpringHelpers;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
///**
// * @author hankun
// */
//@Slf4j
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "true")
//@Component
//public class DomainDynamicDataSourceStrategy implements DynamicDataSourceStrategy {
//
//    public static final String DS_SPLIT = "_";
//    /**
//     * 忽略数据源匹配的线程
//     */
//    private static final String FIRST_THREAD_NAME = "main,RMI TCP Connection";
//
//    public static final String DS_DOMAIN_REROUTE = "kun.ds.reroute.";
//    public static final String DS_REROUTE = "kun.ds.db.domain.reroute.enable";
//
//
//    /**
//     * 分隔字符串
//     */
//    private static final String SPLIT = ",";
//
//
//    @Override
//    public String determineKey(List<String> determineKeys) {
//        LoginUserInfo loginUserInfo = LoginUserContext.get();
//        String threadName = Thread.currentThread().getName();
//
//        if (CollectionUtils.isEmpty(determineKeys)) {
//            String msg = "没有可用数据源，请检查数据源是否配置正确，线程" + threadName + ",用户" + (loginUserInfo == null ? "null" : loginUserInfo) + ",域名" + DomainContext.get();
//            throw new RuntimeException(msg);
//        }
//
//        //域名不为空，则根据域名决定
//        String domain = DomainContext.get();
//        if (!StringUtils.isEmpty(domain)) {
//            String dataSource = domainDataSource(determineKeys, domain);
//            log.debug("数据源,根据域名匹配,域名={},匹配数据源名称={}", domain, dataSource);
//            return dataSource;
//        }
//
//        //域名为空，且符合默认线程，则取第一个返回
//        if (defaultThreadMatch(threadName)) {
//            String dataSource = firstDataSource(determineKeys);
//            log.debug("数据源,域名为空且为{}线程,返回第一个数据源,现成名称={},数据源={}", FIRST_THREAD_NAME, threadName, dataSource);
//            return dataSource;
//        }
//
//        //其他线程，返回错误
//        String msg = "未匹配到数据源，线程" + threadName + ",用户" + (loginUserInfo == null ? "null" : loginUserInfo) + ",域名" + DomainContext.get();
//        log.error(msg);
//        throw new RuntimeException(msg);
//    }
//
//
//    /**
//     * 默认现成匹配逻辑
//     *
//     * @param threadName
//     * @return
//     */
//    private boolean defaultThreadMatch(String threadName) {
//        return Arrays.stream(DomainDynamicDataSourceStrategy.FIRST_THREAD_NAME.split(SPLIT)).anyMatch(threadName::startsWith);
//    }
//
//
//    /**
//     * 查找第一个数据源
//     *
//     * @param dataSources
//     * @return
//     */
//    private String firstDataSource(List<String> dataSources) {
//        return dataSources.stream().findFirst().orElse(null);
//    }
//
//    /**
//     * 查找重定向域名
//     *
//     * @param domain
//     * @return
//     */
//    public static String getReRouteDomain(String domain) {
//
//        if (StringUtils.isEmpty(domain)) {
//            return domain;
//        }
//
//        boolean reRouteEnable = Boolean.parseBoolean(SpringHelpers.getPropertiesWithCache(DS_REROUTE, "false"));
//        if (reRouteEnable) {
//            String reroute = SpringHelpers.getPropertiesWithCache(DS_DOMAIN_REROUTE + domain);
//            if (StringUtils.hasText(reroute)) {
//                log.debug("域名重定向成功,原始域名={},重定向数据源的域名={}", domain, reroute);
//                return reroute;
//            }
//        }
//
//        return domain;
//    }
//
//
//    /**
//     * 根据域名获取线程
//     *
//     * @param dataSources 所有数据源
//     * @return 对应的数据源
//     */
//    private String domainDataSource(List<String> dataSources, String domain) {
//
//        String reRouteDomain = getReRouteDomain(domain);
//        //决策数据源名称
//        Optional<String> matched = dataSources.stream().filter(item -> item.split(DS_SPLIT)[1].equals(reRouteDomain)).findFirst();
//        if (matched.isPresent()) {
//            return matched.get();
//        }
//        //新生成数据源
//        try {
//            String ds = dataSources.get(0).split(DS_SPLIT)[0];
//            DbCreateErrorContext.init();
//            ItemDataSource dataSource = (ItemDataSource) SpringHelpers.context().getBean(DataSourceCacheCreator.class).createDataSource(ds, reRouteDomain);
//            return dataSource.getName();
//        } finally {
//            DbCreateErrorContext.clear();
//        }
//    }
//
//}
