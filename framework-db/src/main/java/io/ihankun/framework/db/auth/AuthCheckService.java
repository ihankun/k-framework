//package io.ihankun.framework.db.auth;
//
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class AuthCheckService {
//
//    public static final String KUN_DB_AUTH_CONFIG = "kun.db.auth.config.";
//
//    public static final String KUN_DB_AUTH_ENABLE = "kun.db.auth.enable";
//
//    private final DbAuthConfigLoader dbAuthConfigLoader;
//
//    public AuthCheckService(DbAuthConfigLoader dbAuthConfigLoader) {
//        this.dbAuthConfigLoader = dbAuthConfigLoader;
//    }
//
//    public void validate(String db, String userName) {
//        if (check(db, userName)) {
//            return;
//        }
//        throw new DbException("无访问权限，数据库用户信息：【" + db + "." + userName + "】，请联系技术中台组进行授权");
//    }
//
//    public boolean check(String db, String userName) {
//        return check(dbAuthConfigLoader.getApplicationName(), db, userName);
//    }
//
//    public boolean isEnable() {
//        return dbAuthConfigLoader.isEnable();
//    }
//
//    public boolean check(String serverName, String db, String userName) {
//        if (!dbAuthConfigLoader.isEnable()) {
//            return true;
//        }
//        String content = dbAuthConfigLoader.getConfig(serverName);
//        if (StringUtils.isEmpty(content)) {
//            return false;
//        }
//        Set<String> authDb = buildAuthDb(content);
//        if (authDb.contains(userName)) {
//            return true;
//        }
//        if (authDb.contains(db + "." + userName)) {
//            return true;
//        }
//        return false;
//    }
//
//    private static Set<String> buildAuthDb(String content) {
//        String[] dbs = content.split(",");
//        Set<String> result = new HashSet<>(dbs.length);
//        for (String s : dbs) {
//            String db = s.trim();
//            if (StringUtils.hasText(db)) {
//                result.add(db);
//            }
//        }
//        return result;
//    }
//}
