//package io.hankun.framework.db.sql;
//
//import io.hankun.framework.core.context.DomainContext;
//import io.hankun.framework.core.context.LoginUserContext;
//import io.hankun.framework.core.context.LoginUserInfo;
//import io.hankun.framework.core.utils.spring.SpringHelpers;
//import io.hankun.framework.db.config.DbConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//import java.lang.reflect.Field;
//import java.sql.Connection;
//import java.util.List;
//
///**
// * @author hankun
// */
//@Component
//@Slf4j
//@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
//public class SqlCommentInterceptor implements Interceptor {
//
//    @Resource
//    private DbConfig config;
//
//    @Resource
//    private MapperStatementHolder holder;
//
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        if (config == null) {
//            return invocation.proceed();
//        }
//
//        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//
//        StringBuilder appendSql = new StringBuilder();
//        //反统方注释
//        if (config.isFanTongFangComment()) {
//            appendSql.append(getUserInfoComment());
//        } else {
//            List<String> fanTongFangCommentByDomains = config.getFanTongFangCommentByDomains();
//            if (!CollectionUtils.isEmpty(fanTongFangCommentByDomains)) {
//                String currentDomain = DomainContext.get();
//                if (!StringUtils.isEmpty(currentDomain) && fanTongFangCommentByDomains.contains(currentDomain)) {
//                    appendSql.append(getUserInfoComment());
//                }
//            }
//        }
//        //mapper定位注释
//        if (config.isMapperComment()) {
//            appendSql.append(getMapperIdComment());
//        }
//
//        if (StringUtils.isEmpty(appendSql.toString())) {
//            return invocation.proceed();
//        }
//
//        // 使用反射设置修改后的 SQL 语句
//        try {
//            BoundSql boundSql = statementHandler.getBoundSql();
//            String sql = appendSql + boundSql.getSql();
//            Field sqlField = boundSql.getClass().getDeclaredField("sql");
//            sqlField.setAccessible(true);
//            sqlField.set(boundSql, sql);
//
//            if (config.isMapperCommentLog()) {
//                log.info("SQL注释:{}", statementHandler.getBoundSql().getSql());
//            }
//        } catch (Exception e) {
//            log.error("修改SQL异常", e);
//        }
//
//
//        return invocation.proceed();
//
//    }
//
//
//    /**
//     * 增加Mapper文件注释
//     */
//    public String getMapperIdComment() {
//        String applicationName = SpringHelpers.getPropertiesWithCache("spring.application.name", "none");
//        String mapperId = holder.getId();
//        String sql = String.format("/*<mapper>%s,%s</mapper>*/\n", applicationName, mapperId);
//        return sql;
//    }
//
//
//    /**
//     * 增加反统方注释
//     */
//    private String getUserInfoComment() {
//
//        String applicationName = SpringHelpers.getPropertiesWithCache("spring.application.name", "default");
//        LoginUserInfo userInfo = LoginUserContext.get();
//        String deviceIp = userInfo == null ? "" : (StringUtils.isEmpty(userInfo.getDeviceIp()) ? "" : userInfo.getDeviceIp());
//        String userId = userInfo == null ? "" : (userInfo.getUserId() == null ? "" : userInfo.getUserId().toString());
//        String userName = userInfo == null ? "" : (StringUtils.isEmpty(userInfo.getUserName()) ? "" : userInfo.getUserName());
//
//        //格式：/*KUN_COMMENT_START#服务名^客户端IP^用户ID^用户名称#KUN_COMMENT_END*/
//        String sql = String.format("/*KUN_COMMENT_START#%s^%s^%s^%s#KUN_COMMENT_END*/\n", applicationName, deviceIp, userId, userName);
//
//        return sql;
//    }
//
//}
