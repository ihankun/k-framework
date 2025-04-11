//package io.ihankun.framework.db.sql;
//
//import io.ihankun.framework.db.config.DbConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class BreakSqlCondition {
//
//    @Resource
//    DbConfig config;
//
//
//    /**
//     * 判断此SQL是否在黑名单熔断里面
//     *
//     * @param sql
//     * @return
//     */
//    public boolean isBreak(String sql) {
//        if (config == null) {
//            return false;
//        }
//        List<String> breakSql = config.getBreakSql();
//        if (breakSql == null || breakSql.isEmpty()) {
//            return false;
//        }
//
//        boolean contains = breakSql.contains(sql);
//        if (contains) {
//            log.info("SQL熔断触发,此SQL禁止执行,SQL={}", sql);
//        }
//        return contains;
//    }
//}
