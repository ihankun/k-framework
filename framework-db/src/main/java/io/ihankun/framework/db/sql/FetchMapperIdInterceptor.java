//package io.ihankun.framework.db.sql;
//
//import io.ihankun.framework.db.config.DbConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.cache.CacheKey;
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.apache.ibatis.session.ResultHandler;
//import org.apache.ibatis.session.RowBounds;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}), @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}), @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}), @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class})})
//public class FetchMapperIdInterceptor implements Interceptor {
//
//    @Resource
//    MapperStatementHolder holder;
//    @Resource
//    private DbConfig config;
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        if (config != null && !config.isMapperIdCache()) {
//            return invocation.proceed();
//        }
//
//        if (invocation.getArgs() == null || invocation.getArgs().length == 0) {
//            return invocation.proceed();
//        }
//
//        Object arg = invocation.getArgs()[0];
//        if (arg != null && arg instanceof MappedStatement) {
//
//            MappedStatement statement = (MappedStatement) arg;
//            long threadId = Thread.currentThread().getId();
//            Long startTime = System.currentTimeMillis();
//
//            try {
//                holder.set(threadId, statement);
//                Object proceed = invocation.proceed();
//                return proceed;
//            } finally {
//                holder.remove(threadId);
//
//                if (config.isSlowSql()) {
//                    Long endTime = System.currentTimeMillis();
//                    Long costTime = endTime - startTime;
//                    if (costTime >= 500) {
//                        log.info("慢SQL日志,mapper={},time={}ms", statement.getId(), costTime);
//                    }
//                }
//
//            }
//        }
//        return invocation.proceed();
//    }
//}
