//package io.ihankun.framework.db.sql;
//
//import io.ihankun.framework.db.config.DbConfig;
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.mapping.SqlCommandType;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//import java.sql.Statement;
//
//
///**
// * @author hankun
// */
//@Component
//@Slf4j
//@Intercepts({
//        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
//        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
//})
//public class SqlUpdateTimeInterceptor implements Interceptor {
//
//    public static final String UPDATE_TIME = "sys_update_time";
//
//    @Resource
//    private DbConfig config;
//
//    @Resource
//    MapperStatementHolder holder;
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        if (!config.isUpdateTimeCheck()) {
//            return invocation.proceed();
//        }
//
//        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//        String sql = statementHandler.getBoundSql().getSql();
//
//        if (StringUtils.isEmpty(sql)) {
//            return invocation.proceed();
//        }
//
//        if (SqlCommandType.UPDATE.equals(holder.getType()) || SqlCommandType.INSERT.equals(holder.getType())) {
//            int re = sql.toLowerCase().indexOf(UPDATE_TIME);
//            if (re < 0) {
//                String error = String.format("SQL更新时间检查,操作未包含更新时间,SQL=%s", sql);
//                log.info(error);
//                throw new DbException(error);
//            }
//        }
//        return invocation.proceed();
//    }
//}
