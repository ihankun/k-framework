//package io.hankun.framework.db.sql;
//
//import io.hankun.framework.db.exceptions.DbException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.apache.ibatis.session.ResultHandler;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.sql.Statement;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//@Intercepts({@Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}), @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}), @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}), @Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),})
//public class BreakSqlInterceptor implements Interceptor {
//
//
//    @Resource
//    MapperStatementHolder holder;
//    @Resource
//    BreakSqlCondition breakSqlCondition;
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        //获取必要参数信息
//        String sqlId = holder.getId(), sql = null;
//        try {
//            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//            if (statementHandler != null) {
//                sql = statementHandler.getBoundSql().getSql();
//            }
//        } catch (Exception e) {
//            log.error("SQL熔断,获取必要参数错误", e);
//        }
//
//        //判断必要参数是否获取成功
//        if (StringUtils.isAnyEmpty(sqlId, sql)) {
//            log.error("SQL熔断,获取必要参数错误,sqlId={},sql={}", sqlId, sql);
//            return invocation.proceed();
//        }
//
//        boolean aBreak = breakSqlCondition.isBreak(sqlId);
//
//        //命中熔断的SQL
//        if (aBreak) {
//            String error = String.format("SQL熔断,因SQL存在问题,已触发熔断策略,MapperId=%s", sqlId);
//            log.error(error);
//            throw new DbException(error);
//        }
//
//        return invocation.proceed();
//
//    }
//
//
//}
