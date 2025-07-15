//package io.ihankun.framework.db.sql;
//
//import io.ihankun.framework.db.config.DbConfig;
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.extern.slf4j.Slf4j;
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
//import java.util.Collection;
//
///**
// * @author hankun
// */
//@Component
//@Slf4j
//@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})})
//public class SqlCountInterceptor implements Interceptor {
//
//    @Resource
//    private DbConfig config;
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        Object[] args = invocation.getArgs();
//        if (args.length < 1 || (!(args[0] instanceof Statement))) {
//            return invocation.proceed();
//        }
//        Statement statement = (Statement) args[0];
//
//        int rows = statement.getMaxRows();
//        int maxRows = config.getMaxRows();
//        if (rows == 0 || rows > maxRows) {
//            statement.setMaxRows(maxRows);
//        }
//
//        Object result = invocation.proceed();
//        if (result instanceof Collection) {
//            Collection<?> res = (Collection<?>) result;
//            if (res.size() >= maxRows) {
//                StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//                String sql = statementHandler.getBoundSql().getSql();
//                String logStr = String.format("SQL查询结果限制,查询结果超过%s行,被终止,SQL=%s", maxRows, sql);
//                log.info(logStr);
//                throw new DbException(logStr);
//            }
//        }
//        return result;
//    }
//}
