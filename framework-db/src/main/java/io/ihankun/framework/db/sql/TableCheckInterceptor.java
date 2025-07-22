//package io.ihankun.framework.db.sql;
//
//import io.ihankun.framework.core.invocation.InvocationTableChecker;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//
//import java.sql.Connection;
//
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//@ConditionalOnProperty(prefix = "kun.invocation.config.auth", value = "enabled", havingValue = "true")
//@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
//public class TableCheckInterceptor implements Interceptor {
//
//    @Autowired(required = false)
//    private InvocationTableChecker invocationTableChecker;
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        if (invocationTableChecker == null) {
//            return invocation.proceed();
//        }
//        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//        Connection connection = (Connection) invocation.getArgs()[0];
//        String sql = statementHandler.getBoundSql().getSql();
//        String url = connection.getMetaData().getURL();
//        invocationTableChecker.validate(url, sql);
//        return invocation.proceed();
//    }
//
//
//}
