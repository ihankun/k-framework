//package io.ihankun.framework.db.sql;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import com.baomidou.dynamic.datasource.ds.ItemDataSource;
//import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
//import io.ihankun.framework.core.context.DomainContext;
//import io.ihankun.framework.core.context.LoginUserContext;
//import io.ihankun.framework.core.context.LoginUserInfo;
//import io.ihankun.framework.db.limit.RateFilter;
//import io.ihankun.framework.db.limit.RateProperties;
//import io.ihankun.framework.db.limit.RateSnapshotEngine;
//import io.ihankun.framework.db.limit.element.RateLimitedElement;
//import io.ihankun.framework.db.limit.element.RateSnapshotElement;
//import io.ihankun.framework.core.utils.spring.SpringHelpers;
//import io.ihankun.framework.db.dynamic.DomainDynamicDataSourceStrategy;
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.time.DateFormatUtils;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.apache.ibatis.session.ResultHandler;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import java.sql.Statement;
//import java.util.Date;
//import java.util.Optional;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//@Intercepts({
//        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
//        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
//        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
//        @Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),})
//public class RateDatabaseInterceptor implements Interceptor, RateFilter {
//
//    @Resource
//    RateSnapshotEngine engine;
//    @Resource
//    RateProperties properties;
//
//    @Resource
//    MapperStatementHolder holder;
//
//
//    @Override
//    public String businessKeyPrefix() {
//        return "DataBase";
//    }
//
//    @Override
//    public int maxQueueSize(String businessKey) {
//        int defaultMaxActive = 20;
//        try {
//            String configMax = SpringHelpers.getPropertiesWithCache("spring.datasource.dynamic.druid.maxActive");
//            if (StringUtils.isNotEmpty(configMax)) {
//                defaultMaxActive = Integer.parseInt(configMax);
//            }
//            //根据不同的BusinessKey获取不同连接池的队列大小
//            DataSource dataSource = SpringHelpers.context().getBean(DynamicRoutingDataSource.class).getDataSource(businessKey);
//            if (dataSource != null) {
//                return ((DruidDataSource) ((ItemDataSource) dataSource).getRealDataSource()).getMaxActive();
//            }
//
//            return defaultMaxActive;
//        } catch (Exception e) {
//            log.info("自适应限流,获取DynamicRoutingDataSource失败,Druid连接池最大数使用默认值" + defaultMaxActive);
//            return defaultMaxActive;
//        }
//    }
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        String businessKeyPrefix = businessKeyPrefix();
//
//        //关闭状态，不进行后续逻辑判断
//        if (!properties.getConfig(businessKeyPrefix).isEnable()) {
//            return invocation.proceed();
//        }
//
//        //获取必要参数信息
//        String sqlId = holder.getId(), sql = null, databaseId = null;
//        try {
//            String ds = Optional.ofNullable(DynamicDataSourceContextHolder.peek()).orElse("master");
//            databaseId = ds + DomainDynamicDataSourceStrategy.DS_SPLIT + DomainDynamicDataSourceStrategy.getReRouteDomain(DomainContext.get());
//            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//            if (statementHandler != null) {
//                sql = statementHandler.getBoundSql().getSql();
//            }
//        } catch (Exception e) {
//            log.error("自适应限流,获取必要参数错误", e);
//        }
//
//        //判断必要参数是否获取成功
//        if (StringUtils.isAnyEmpty(sql, sqlId, databaseId)) {
//            log.error("自适应限流,获取必要参数错误,databaseId={},sqlId={},sql={}", databaseId, sqlId, sql);
//            return invocation.proceed();
//        }
//
//        //限流处理
//        LoginUserInfo userInfo = LoginUserContext.get();
//        String userId = userInfo == null ? "" : (userInfo.getUserId() == null ? "" : userInfo.getUserId().toString());
//        String domain = DomainContext.get() == null ? "" : DomainContext.get();
//        String businessKey = String.format(businessKeyPrefix + RateProperties.BUSINESS_SPLIT + "%s", databaseId);
//
//        RateSnapshotElement element = new RateSnapshotElement(sqlId, domain, userId);
//        RateLimitedElement limited = engine.limited(businessKey, element, properties);
//        if (limited != null) {
//            String error = String.format("触发SQL自适应限流,策略:%s,资源:%s,释放时间:%s,SQL内容:%s", limited.getLimitStrategy().getDesc(), limited.getLimitedKey(), DateFormatUtils.format(new Date(limited.getExpireTime()), "yyyy-MM-dd HH:mm:ss"), sql);
//            log.error(error);
//            throw new DbException(error);
//        }
//
//        //执行SQL本身事务
//        try {
//            engine.push(businessKey, element);
//            return invocation.proceed();
//        } finally {
//            engine.remove(businessKey, element);
//        }
//    }
//
//
//}
