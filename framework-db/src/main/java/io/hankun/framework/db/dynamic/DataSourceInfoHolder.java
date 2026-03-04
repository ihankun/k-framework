//package io.hankun.framework.db.dynamic;
//
//import com.alibaba.druid.util.JdbcUtils;
//import io.hankun.framework.db.build.BuildInfoHolder;
//import io.hankun.framework.db.build.DsPropertiesProvider;
//import io.hankun.framework.db.build.config.DsConfig;
//import io.hankun.framework.db.build.connect.ConnectionConfigBuilder;
//import io.hankun.framework.db.build.connect.entity.ConnectionConfig;
//import io.hankun.framework.db.build.ds.DataSourceConfig;
//import io.hankun.framework.db.config.DataSourceConstant;
//import io.hankun.framework.db.dynamic.bean.DataSourceInfo;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.jasypt.util.text.AES256TextEncryptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ObjectUtils;
//
//import javax.annotation.Resource;
//import javax.validation.constraints.NotEmpty;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class DataSourceInfoHolder {
//
//    @Resource
//    Environment environment;
//
//    private static final String URL_PREFIX = "jdbc:postgresql://";
//
//    private static final String URL_SUFFIX = "?useUnicode=true&characterEncoding=utf8&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai";
//
//    /**
//     * 设置environment，仅测试用
//     *
//     * @param environment environment
//     */
//    public void mockEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//
//    @Setter
//    @Autowired(required = false)
//    private ConnectionConfigBuilder connectionConfigBuilder;
//
//    @Setter
//    @Autowired(required = false)
//    private DsPropertiesProvider kunDsPropertiesProvider;
//
//    @Setter
//    @Autowired
//    private DsConfig kunDsConfig;
//
//    /**
//     * 根据域名、数据库名称(HIS_MASTER等)、用户名，获取匹配的数据库ip端口密码信息
//     * 该接口仅从nacos配置中获取信息
//     * 受到老环境密码修改的影响
//     * 在新环境可正常使用，在老环境下可能返回错误的密码
//     *
//     * @param domain   域名
//     * @param db       数据库名称(HIS_MASTER等)
//     * @param userName 用户名
//     * @return com.kun.core.db.dynamic.bean.kunDataSourceInfo
//     */
//    public DataSourceInfo getDataSourceInfo(@NotEmpty String domain, @NotEmpty String db, @NotEmpty String userName) {
//        return getDataSourceInfo(domain, db, userName, null, false);
//    }
//
//    /**
//     * 根据域名、数据库名称(HIS_MASTER等)、用户名，获取匹配的数据库ip端口密码信息
//     * 该接口仅从nacos配置中获取信息
//     * 在新环境可正常使用
//     * 如果checkPassword为false，在老环境下可能返回错误的密码
//     * 如果为true，会在返回前检查密码正常性，返回正常的密码，但是效率低于不检测（需要连接数据库检测密码是否正确），
//     *
//     * @param domain        域名
//     * @param db            数据库名称(HIS_MASTER等)
//     * @param userName      用户名
//     * @param pgDb          pg中的数据库名称（chis，chisapp），如果不检查密码正常性可传null
//     * @param checkPassword 是否确认密码正常性
//     * @return com.kun.core.db.dynamic.bean.kunDataSourceInfo
//     */
//    public DataSourceInfo getDataSourceInfo(@NotEmpty String domain, @NotEmpty String db, @NotEmpty String userName, String pgDb, Boolean checkPassword) {
//        if (connectionConfigBuilder != null && kunDsPropertiesProvider != null) {
//            if (ObjectUtils.isEmpty(pgDb)) {
//                DataSourceInfo kunDataSourceInfo = getkunDataSourceInfoNew(domain, db, userName, "chis");
//                if (kunDataSourceInfo != null) {
//                    return kunDataSourceInfo;
//                }
//                return getkunDataSourceInfoNew(domain, db, userName, "cdrapp");
//            } else {
//                return getkunDataSourceInfoNew(domain, db, userName, pgDb);
//            }
//        }
//        boolean activeReroute = Boolean.parseBoolean(environment.getProperty(DomainDynamicDataSourceStrategy.DS_REROUTE));
//        if (activeReroute) {
//            String reroute = environment.getProperty(DomainDynamicDataSourceStrategy.DS_DOMAIN_REROUTE + domain);
//            if (reroute != null) {
//                log.info("DataSourceInfoHolder.getDataSourceInfo,reroute,domain={},reroute={}", domain, reroute);
//                domain = reroute;
//            }
//        }
//
//        final String pre = DataSourceConstant.DS_PREFIX + domain + DataSourceConstant.DS_POINT;
//
//        String ipPort = environment.getProperty(pre + db);
//        if (StringUtils.isBlank(ipPort)) {
//            log.error("DataSourceInfoHolder.getDataSourceInfo.ipPort.get.fail,domain={},db={},user={}", domain, db, userName);
//            return null;
//        }
//
//        String password = environment.getProperty(pre + userName + DataSourceConstant.DS_POINT + db);
//        if (StringUtils.isBlank(password)) {
//            log.warn("DataSourceInfoHolder.getDataSourceInfo.get.password.from.database.null,domain={},db={},user={}", domain, db, userName);
//
//            password = environment.getProperty(pre + userName);
//            if (StringUtils.isBlank(password)) {
//                log.error("DataSourceInfoHolder.getDataSourceInfo.get.password.null,domain={},db={},user={}", domain, db, userName);
//            }
//        }
//        String key = environment.getProperty(DataSourceConstant.NEW_PRIVATE_KEY + domain);
//        if (org.springframework.util.StringUtils.isEmpty(key)) {
//            key = environment.getProperty(DataSourceConstant.PUBLIC_KEY);
//        }
//        password = decryptPass(key, password);
//        log.info("DataSourceInfoHolder.getDataSourceInfo.get.password,domain={},db={},user={}", domain, db, userName);
//        if (checkPassword) {
//            if (!checkPassword(ipPort, pgDb, userName, password)) {
//                log.info("DataSourceInfoHolder.getDataSourceInfo.check.password.failed,domain={},db={},user={}", domain, db, userName);
//                password = null;
//            }
//        }
//        if (StringUtils.isBlank(password)) {
//            String dbKey = environment.getProperty(DataSourceConstant.DB_BUILD_PRE + domain);
//            String useEnv = environment.getProperty(DataSourceConstant.USE_ENV_KEY);
//            if (!StringUtils.isBlank(dbKey)) {
//                if (key == null) {
//                    key = "";
//                }
//                if (Boolean.TRUE.toString().equals(useEnv)) {
//                    key = "";
//                }
//                if (StringUtils.isBlank(key)) {
//                    log.warn("DataSourceInfoHolder.getDataSourceInfo.get.password.env.key.null,domain={},db={},user={}", domain, db, userName);
//                }
//                password = DataSourceCacheCreator.buildPass(key, dbKey, userName);
//                if (checkPassword && (!checkPassword(ipPort, pgDb, userName, password))) {
//                    log.info("DataSourceInfoHolder.getDataSourceInfo.check.password.failed,domain={},db={},user={}", domain, db, userName);
//                    password = null;
//                }
//            } else {
//                log.error("DataSourceInfoHolder.getDataSourceInfo.get.password.db.key.null,domain={},db={},user={}", domain, db, userName);
//            }
//        }
//        String[] ipPortSplit = ipPort.split(":");
//        return DataSourceInfo.builder().ip(ipPortSplit[0]).port(ipPortSplit[1]).password(password).build();
//    }
//
//
//    private DataSourceInfo getkunDataSourceInfoNew(String domain, String db, String userName, String pgDb) {
//        DataSourceConfig dataSourceConfig = new DataSourceConfig();
//        dataSourceConfig.setDomain(domain);
//        dataSourceConfig.setServiceName(kunDsConfig.getCurrentService());
//        dataSourceConfig.setDbMark(db);
//        dataSourceConfig.setDs("ds");
//        dataSourceConfig.setDb(pgDb);
//        dataSourceConfig.setUser(userName);
//        dataSourceConfig.setSchema("");
//        kunDsPropertiesProvider.initDs(dataSourceConfig);
//        BuildInfoHolder buildInfoHolder = new BuildInfoHolder();
//        ConnectionConfig config = connectionConfigBuilder.build(dataSourceConfig, buildInfoHolder);
//        if (config == null) {
//            log.error("DataSourceInfoHolder.getDataSourceInfo.build.config.fail,message={}",
//                    buildInfoHolder.getErrorMessage(dataSourceConfig.getDs(), domain));
//            return null;
//        }
//        String ipPort = connectionConfigBuilder.getAddress(dataSourceConfig);
//        String[] ipPortSplit = ipPort.split(":");
//        return DataSourceInfo.builder().ip(ipPortSplit[0]).port(ipPortSplit[1]).password(config.getPassword()).build();
//    }
//
//    private static boolean checkPassword(String ipPort, String db, String userName, String pass) {
//        String url = URL_PREFIX + ipPort + "/" + db + URL_SUFFIX;
//        Connection connection = null;
//        try {
//            Properties info = new Properties();
//            info.put("user", userName);
//            info.put("password", pass);
//            info.put("loginTimeout", "3");
//            connection = DriverManager.getConnection(url, info);
//            return true;
//        } catch (SQLException e) {
//            return false;
//        } finally {
//            JdbcUtils.close(connection);
//        }
//    }
//
//    private static String decryptPass(String key, String pass) {
//        if (StringUtils.isNotBlank(key)) {
//            // 解密密码
//            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
//            textEncryptor.setPassword(key);
//            try {
//                pass = textEncryptor.decrypt(pass);
//                log.info("DataSourceInfoHolder.getDataSourceInfo.pass.decrypt.success!");
//            } catch (Exception e) {
//                log.info("DataSourceInfoHolder.getDataSourceInfo.pass.decrypt.fail!");
//            }
//        } else {
//            log.info("DataSourceInfoHolder.getDataSourceInfo.encryptor.key.not.find!");
//        }
//        return pass;
//    }
//}
