//package io.hankun.framework.db.auth;
//
//
//import io.hankun.framework.core.context.upstream.UpstreamInfo;
//import io.hankun.framework.core.context.upstream.UpstreamInfoContext;
//import io.hankun.framework.core.invocation.InvocationCollector;
//import io.hankun.framework.core.invocation.InvocationTableChecker;
//import io.hankun.framework.core.invocation.entity.ServiceTableRule;
//import io.hankun.framework.db.config.DataSourceConstant;
//import io.hankun.framework.db.dynamic.DataSourceCacheCreator;
//import io.hankun.framework.db.exceptions.TableAuthException;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//@ConditionalOnProperty(prefix = "kun.invocation.config.auth", value = "enabled", havingValue = "true")
//public class ServiceTableChecker implements InvocationTableChecker {
//
//    @Getter
//    @AllArgsConstructor
//    public static class DbInfo {
//        private final String db;
//        private final String schema;
//
//        public String buildUri() {
//            return UpstreamInfo.PREFIX + db + UpstreamInfo.PREFIX + schema;
//        }
//    }
//
//
//    @AllArgsConstructor
//    public static class SchemaAuth {
//        @Getter
//        private final Integer auth;
//        private final Map<String, Integer> tableAuth;
//
//        public Integer getAuth(String table) {
//            return tableAuth.get(table);
//        }
//    }
//
//    @Autowired(required = false)
//    InvocationCollector kunInvocationCollector;
//
//    private volatile Map<String, Map<String, SchemaAuth>> authMap = new HashMap<>();
//
//    @Getter
//    private volatile boolean enable = false;
//
//    private final Map<String, DbInfo> dbInfoMap = new HashMap<>();
//
//    @Override
//    public void updateRule(ServiceTableRule rule) {
//        if (rule == null) {
//            return;
//        }
//        Map<String, Map<String, SchemaAuth>> newAuthMap = new HashMap<>();
//        if (!CollectionUtils.isEmpty(rule.getDbs())) {
//            for (ServiceTableRule.DbRule dbRule : rule.getDbs()) {
//                Map<String, SchemaAuth> dbMap = new HashMap<>();
//                newAuthMap.put(dbRule.getDb(), dbMap);
//                if (!CollectionUtils.isEmpty(dbRule.getSchemas())) {
//                    for (ServiceTableRule.SchemaRule schemaRule : dbRule.getSchemas()) {
//                        Map<String, Integer> schemaMap = new HashMap<>();
//                        Integer schemaAuth = schemaRule.getAuth();
//                        dbMap.put(schemaRule.getSchema(), new SchemaAuth(schemaAuth, schemaMap));
//                        if (!CollectionUtils.isEmpty(schemaRule.getTables())) {
//                            for (ServiceTableRule.TableRule tableRule : schemaRule.getTables()) {
//                                schemaMap.put(tableRule.getTable(), tableRule.getAuth());
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        enable = rule.getEnable() != null && rule.getEnable();
//        authMap = newAuthMap;
//    }
//
//    @Override
//    public void validate(String url, String sql) {
//        if (kunInvocationCollector == null) {
//            return;
//        }
//        TableRuleResult result;
//        DbInfo dbInfo = getDbInfo(url);
//        try {
//            Map<String, Integer> opMap = TableFetcher.getTableOp(sql);
//            result = validateDb(dbInfo, opMap);
//            if (result.isSuc()) {
//                for (Map.Entry<String, Integer> entry : opMap.entrySet()) {
//                    kunInvocationCollector.collectInvocation(UpstreamInfoContext.DATABASE_SERVICE,
//                            kunInvocationCollector.getCurrentService(), UpstreamInfoContext.get(),
//                            dbInfo.buildUri() + UpstreamInfo.PREFIX +
//                                    entry.getKey() + UpstreamInfo.PREFIX + TableOp.codeToString(entry.getValue()));
//
//                }
//                return;
//            }
//        } catch (Throwable e) {
//            kunInvocationCollector.collectFailedInvocation(UpstreamInfoContext.DATABASE_SERVICE,
//                    kunInvocationCollector.getCurrentService(), UpstreamInfoContext.get(), dbInfo.buildUri(),
//                    "服务【" + kunInvocationCollector.getCurrentService() + "】处理异常：" + sql, null);
//            log.error("table.auth.validate.error,url={},sql={},e={}", url, sql, e.getMessage());
//            return;
//        }
//        kunInvocationCollector.collectFailedInvocation(UpstreamInfoContext.DATABASE_SERVICE,
//                kunInvocationCollector.getCurrentService(), UpstreamInfoContext.get(), dbInfo.buildUri(),
//                "服务【" + kunInvocationCollector.getCurrentService() + "】" + result.getMessage(), null);
//        log.error("table.auth.validate.failed,url={},sql={},result={}", url, sql, result);
//        throw new TableAuthException(result.getMessage());
//    }
//
//
//    TableRuleResult validateDb(DbInfo dbInfo, Map<String, Integer> opMap) {
//        if (!enable) {
//            return TableRuleResult.suc();
//        }
//        for (Map.Entry<String, Integer> entry : opMap.entrySet()) {
//            TableRuleResult result = check(dbInfo.getDb(), dbInfo.getSchema(), entry.getKey(), entry.getValue());
//            if (!result.isSuc()) {
//                return result;
//            }
//        }
//        return TableRuleResult.suc();
//    }
//
//
//    private TableRuleResult check(String db, String schema, String table, int code) {
//        Map<String, SchemaAuth> dbMap = authMap.get(db);
//        if (dbMap == null) {
//            return TableRuleResult.fail("缺少对数据库【" + db + "】的权限");
//        }
//        SchemaAuth schemaAuthInfo = dbMap.get(schema);
//        if (schemaAuthInfo == null) {
//            return TableRuleResult.fail("缺少对模式【" + db + "." + schema + "】的权限");
//        }
//        Integer schemaAuth = schemaAuthInfo.getAuth();
//        if (schemaAuth != null) {
//            code = TableOp.check(code, schemaAuth);
//        }
//        if (code == 0) {
//            return TableRuleResult.suc();
//        }
//        Integer auth = schemaAuthInfo.getAuth(table);
//        if (auth == null) {
//            return TableRuleResult.fail("缺少对表【" + db + "." + schema + "." + table + "】的权限");
//        }
//        int result = TableOp.check(code, auth);
//        if (result != 0) {
//            return TableRuleResult.fail("缺少对表【" + db + "." + schema + "." + table + "】的【" +
//                    TableOp.desc(result) + "】权限");
//        }
//        return TableRuleResult.suc();
//    }
//
//    private DbInfo getDbInfo(String url) {
//        return dbInfoMap.computeIfAbsent(url, s -> {
//            String db = getDatabase(s);
//            String schema = getSchema(s);
//            return new DbInfo(db, schema);
//        });
//    }
//
//    public static String getDatabase(String url) {
//        return DataSourceCacheCreator.getDb(url);
//    }
//
//    public static String getSchema(String url) {
//        int start = url.indexOf(DataSourceConstant.DS_SCHEMA);
//        if (start < 0) {
//            return null;
//        }
//        int end = url.indexOf(DataSourceConstant.DS_SPLIT_OF_PARAMETER, start);
//        if (end > 0) {
//            return url.substring(start + DataSourceConstant.DS_SCHEMA.length(), end);
//        }
//        return url.substring(start + DataSourceConstant.DS_SCHEMA.length());
//    }
//}
