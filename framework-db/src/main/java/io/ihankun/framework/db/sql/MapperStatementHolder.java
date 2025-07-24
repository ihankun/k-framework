//package io.ihankun.framework.db.sql;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlCommandType;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class MapperStatementHolder {
//
//    private ConcurrentHashMap<Long, MappedStatement> map = new ConcurrentHashMap(2000);
//
//
//    /**
//     * 缓存
//     *
//     * @param threadId
//     * @param statement
//     */
//    public void set(Long threadId, MappedStatement statement) {
//        map.put(threadId, statement);
//    }
//
//    /**
//     * 移除
//     *
//     * @param threadId
//     */
//    public void remove(Long threadId) {
//        map.remove(threadId);
//    }
//
//    /**
//     * 获取MapperId
//     *
//     * @return
//     */
//    public String getId() {
//        MappedStatement statement = map.get(Thread.currentThread().getId());
//        if (statement != null) {
//            return statement.getId();
//        }
//        return null;
//    }
//
//    /**
//     * 获取SQL类型
//     *
//     * @return
//     */
//    public SqlCommandType getType() {
//        MappedStatement statement = map.get(Thread.currentThread().getId());
//        if (statement != null) {
//            return statement.getSqlCommandType();
//        }
//        return null;
//    }
//
//}
