//package io.ihankun.framework.db.dynamic;
//
//import com.alibaba.druid.util.JdbcUtils;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
//import io.ihankun.framework.db.config.DataSourceConstant;
//import io.ihankun.framework.db.dynamic.bean.DataSourceBuildProperty;
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.jasypt.util.text.AES256TextEncryptor;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import org.springframework.core.env.Environment;
//import org.springframework.core.env.PropertySource;
//import org.springframework.core.env.StandardEnvironment;
//import org.springframework.stereotype.Component;
//import org.springframework.util.DigestUtils;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.nio.charset.StandardCharsets;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author hankun
// */
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "true")
//@Component
//@Slf4j
//public class DataSourceCacheCreator implements ApplicationEventPublisherAware {
//
//    private final Map<String, Object> lockers = new ConcurrentHashMap<>();
//
//    @Resource
//    private DataSource dataSource;
//
//    @Resource
//    Environment environment;
//
//    @Resource
//    private PropertiesHolder propertiesHolder;
//
//    @Resource
//    private DefaultDataSourceCreator dataSourceCreator;
//
//    /**
//     * 从url的获取数据库名称
//     *
//     * @param url 数据库地址
//     * @return 数据库名称
//     */
//    public static String getDb(String url) {
//        int start = url.indexOf(DataSourceConstant.DS_DOMAIN_START) + DataSourceConstant.DS_DOMAIN_START.length();
//        int s = url.indexOf(DataSourceConstant.DS_DOMAIN_END, start) + 1;
//        int e = url.indexOf(DataSourceConstant.DS_QUESTION, s);
//        if (s < 1) {
//            return null;
//        }
//        if (e < 0) {
//            return url.substring(s);
//        }
//        return url.substring(s, e);
//    }
//
//    /**
//     * 从url的获取数据库地址
//     *
//     * @param url 数据库地址
//     * @return 数据库名称
//     */
//    public static String getAddr(String url) {
//        int start = url.indexOf(DataSourceConstant.DS_DOMAIN_START) + DataSourceConstant.DS_DOMAIN_START.length();
//        int end = url.indexOf(DataSourceConstant.DS_DOMAIN_END, start);
//        if (start < 1 || end < 0) {
//            return null;
//        }
//        return url.substring(start, end);
//    }
//
//
//    /**
//     * @param ds     数据源别名
//     * @param domain 域名
//     * @return 数据源
//     */
//    public DataSource createDataSource(String ds, String domain) {
//
//        String datasourceName = ds + DomainDynamicDataSourceStrategy.DS_SPLIT + domain;
//        lockers.putIfAbsent(datasourceName, new Object());
//        synchronized (lockers.get(datasourceName)) {
//            if (!(dataSource instanceof DynamicRoutingDataSource)) {
//                log.error("创建数据源失败,类型不匹配,期望的数据源类型为DynamicRoutingDataSource,当前为{}", dataSource.getClass());
//                return null;
//            }
//            DataSource target = ((DynamicRoutingDataSource) dataSource).getDataSources().get(datasourceName);
//            if (target != null) {
//                return target;
//            }
//            DataSourceProperty property = propertiesHolder.getModel(ds);
//            if (property == null) {
//                String error = String.format("动态数据源创建失败,没有找到此数据源的配置,datasourceName=%s", datasourceName);
//                log.error(error);
//                throw new DbException(error);
//            }
//            if (buildDataSource(property, environment, domain, ds)) {
//                //生产对应数据源
//                property.setPoolName(datasourceName);
//                String db = getDb(property.getUrl());
//                DataSource source = dataSourceCreator.createDataSource(property);
//                ((DynamicRoutingDataSource) dataSource).addDataSource(datasourceName, source);
//                applicationEventPublisher.publishEvent(new DataSourceBuildEvent(this, source));
//                log.debug("动态创建数据源成功,dsName={},db={},userName={}", datasourceName, db, property.getUsername());
//                return source;
//            }
//
//            String error = String.format("动态数据源创建失败,dsName=%s,db=%s,alias=%s,user=%s,原因=%s", datasourceName, getDb(property.getUrl()), ds, property.getUsername(), DbCreateErrorContext.get());
//            log.error(error);
//            throw new DbException(error);
//        }
//    }
//
//
//    /**
//     * 更新数据源配置
//     *
//     * @param property    待更新的数据源配置
//     * @param environment 环境
//     * @param domain      域名
//     * @param alias       别名
//     * @return 是否成功
//     */
//    public static boolean buildDataSource(DataSourceProperty property, Environment environment, String domain, String alias) {
//        final String url = property.getUrl();
//        final String pre = DataSourceConstant.DS_PREFIX + domain + DataSourceConstant.DS_POINT;
//        final String userName = property.getUsername();
//        final String useEnv = environment.getProperty(DataSourceConstant.USE_ENV_KEY);
//
//        if (StringUtils.isEmpty(userName)) {
//            log.error("DataSourceCacheCreator.buildDataSource.user.is.empty,property={},alias={}", pre + userName, alias);
//            return false;
//        }
//        //读取数据库配置
//        String dbMark = getAddr(url);
//        //先尝试从Nacos中获取该域名的key
//        boolean nDomainKeyFlag = environment.containsProperty(DataSourceConstant.NEW_PRIVATE_KEY + domain);
//        String key;
//        if (nDomainKeyFlag) {
//            key = environment.getProperty(DataSourceConstant.NEW_PRIVATE_KEY + domain);
//        } else {
//            //从环境变量中获取该环境的key
//            key = environment.getProperty(DataSourceConstant.PUBLIC_KEY);
//            if (Boolean.TRUE.toString().equals(useEnv)) {
//                key = "";
//            }
//        }
//        if (StringUtils.isEmpty(key)) {
//            key = "";
//            log.debug("DataSourceCacheCreator.buildDataSource.try.generate.pass.with.empty.env.key!property={},user={}", property, userName);
//        }
//
//        DataSourceBuildProperty dataSourceBuildProperty = new DataSourceBuildProperty(domain, alias, dbMark, key);
//        //新版本
//        log.debug("DataSourceCacheCreator.try.config.with.v2,property={}", dataSourceBuildProperty);
//        try {
//            if (buildWithV2(property, environment, pre, dataSourceBuildProperty)) {
//                return true;
//            }
//        } catch (Throwable e) {
//            log.error("DataSourceCacheCreator.build.with.v2.error,e=", e);
//        }
//        return false;
//    }
//
//    private static boolean buildWithV2(DataSourceProperty property, Environment environment, String pre, DataSourceBuildProperty dataSourceBuildProperty) {
//        String addr = environment.getProperty(pre + dataSourceBuildProperty.getDbMark());
//        String pass = environment.getProperty(pre + property.getUsername());
//        if (StringUtils.isEmpty(addr)) {
//            log.debug("DataSourceCacheCreator.buildDataSource.v2.addr.miss,property={}", dataSourceBuildProperty);
//            DbCreateErrorContext.error("未配置数据库地址：[", dataSourceBuildProperty.getDbMark(), "]");
//            return false;
//        }
//        //使用配置的医院特定密码
//        String newPass = environment.getProperty(pre + property.getUsername() + DataSourceConstant.DS_POINT + dataSourceBuildProperty.getDbMark());
//        if (!StringUtils.isEmpty(newPass)) {
//            log.debug("DataSourceCacheCreator.buildDataSource.v2.try.pass,property={}", dataSourceBuildProperty);
//            if (tryUpdateProperty(property, environment, dataSourceBuildProperty, addr, newPass)) {
//                return true;
//            }
//            log.debug("DataSourceCacheCreator.buildDataSource.v2.try.hospital.pass.fail,property={}", dataSourceBuildProperty);
//        }
//        //密码不空
//        if (!StringUtils.isEmpty(pass)) {
//            if (tryUpdateProperty(property, environment, dataSourceBuildProperty, addr, pass)) {
//                return true;
//            }
//            log.debug("DataSourceCacheCreator.buildDataSource.v2.try.pass.fail,property={}", dataSourceBuildProperty);
//        } else {
//            log.debug("DataSourceCacheCreator.buildDataSource.v2.origin.pass.miss,property={}", dataSourceBuildProperty);
//        }
//        //尝试生成密码
//        if (tryGeneratePass(property, environment, dataSourceBuildProperty, addr)) {
//            return true;
//        }
//        log.debug("DataSourceCacheCreator.buildDataSource.v2.try.generate.pass.fail,property={}", dataSourceBuildProperty);
//        return false;
//    }
//
//    private static boolean tryGeneratePass(DataSourceProperty property, Environment environment, DataSourceBuildProperty dataSourceBuildProperty, String addr) {
//        if (StringUtils.isEmpty(addr)) {
//            return false;
//        }
//        String dbKey = environment.getProperty(DataSourceConstant.DB_BUILD_PRE + dataSourceBuildProperty.getDomain());
//        String userName = property.getUsername();
//        String key = dataSourceBuildProperty.getEnvKey();
//        if (!StringUtils.isEmpty(dbKey)) {
//            String pass = buildPass(key, dbKey, userName);
//            log.debug("DataSourceCacheCreator.buildDataSource.try.generate.pass!dbInfo={},user={}", dataSourceBuildProperty, userName);
//            return tryUpdateProperty(property, environment, dataSourceBuildProperty, addr, pass);
//        } else {
//            log.debug("DataSourceCacheCreator.buildDataSource.does.not.try.generate.db.key.is.empty!property={},user={}", dataSourceBuildProperty, userName);
//        }
//        return false;
//    }
//
//    private static boolean tryUpdateProperty(DataSourceProperty property, Environment environment, DataSourceBuildProperty dataSourceBuildProperty, String addr, String pass) {
//        String url = property.getUrl();
//        String userName = property.getUsername();
//        log.debug("DataSourceCacheCreator.buildDataSource.start.update.url,property={},addr={}", dataSourceBuildProperty, addr);
//        //更新数据库地址和密码
//        String schemaList = environment.getProperty(DataSourceConstant.DS_SCHEMA_DIS);
//        boolean needSchema = checkSchema(userName, schemaList);
//        if (!needSchema) {
//            log.debug("DataSourceCacheCreator.buildDataSource.not.add.schema!,user={},list={}", userName, schemaList);
//        }
//        url = updateUrl(url, addr);
//        pass = decryptPass(dataSourceBuildProperty.getEnvKey(), pass);
//        int re = checkAndUpdateDataSource(url, userName, pass, needSchema, property);
//        if (re == 0) {
//            updateConfig(property, environment, dataSourceBuildProperty.getDomain(), userName, dataSourceBuildProperty.getAlias());
//            log.debug("DataSourceCacheCreator.buildDataSource.update.url.success,property={},addr={}", dataSourceBuildProperty, addr);
//            return true;
//        }
//        if (re == 1) {
//            log.debug("DataSourceCacheCreator.buildDataSource.password.authentication.fail,userName={},property={},errorPass={}", userName, dataSourceBuildProperty, pass);
//        }
//        return false;
//    }
//
//    public static void updateConfig(DataSourceProperty property, Environment environment, String domain, String userName, String alias) {
//        try {
//            String disableList = environment.getProperty("kun.ds.dynamic.seata.disable");
//            String[] disables;
//            if (!StringUtils.isEmpty(disableList)) {
//                disables = disableList.split(",");
//            } else {
//                disables = new String[]{DataSourceConstant.SLAVE};
//            }
//            for (String disable : disables) {
//                disable = disable.trim();
//                if (disable.equals(alias)) {
//                    property.setSeata(false);
//                }
//            }
//            log.debug("DataSourceCacheCreator.buildDataSource.update.seata.finish,alias={},domain={},seata={}", alias, domain, property.getSeata());
//            String serviceName = environment.getProperty("spring.application.name");
//            //druid连接池配置注入
//            Map<String, Object> druidProperties = getProperties(environment, DataSourceConstant.KUN_DS_DRUID + serviceName + "." + userName + ".");
//            log.debug("DataSourceCacheCreator.buildDataSource.load.config,datas={}", druidProperties);
//            DruidConfig config = new DruidConfig();
//            config.setWall(convertProperties(druidProperties, a -> a.startsWith("wall.")));
//            config.setStat(convertProperties(druidProperties, a -> a.startsWith("stat.")));
//            config.setSlf4j(convertProperties(druidProperties, a -> a.startsWith("slf4j.")));
//            property.setDruid(config);
//            log.debug("DataSourceCacheCreator.buildDataSource.update.druid.suc,datas={}", JSON.toJSONString(config, SerializerFeature.NotWriteDefaultValue));
//        } catch (Throwable e) {
//            log.error("DataSourceCacheCreator.buildDataSource.update.config.fail,e=", e);
//        }
//
//    }
//
//    private static <T> void setValue(T data, Map<String, String> properties) {
//        Class<?> tClass = data.getClass();
//        for (Map.Entry<String, String> property : properties.entrySet()) {
//            try {
//                Field field = tClass.getDeclaredField(property.getKey());
//                boolean accessible = field.isAccessible();
//                field.setAccessible(true);
//                Class<?> filedClass = field.getType();
//                field.set(data, convertValue(property.getValue(), filedClass));
//                field.setAccessible(accessible);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                log.error("DataSourceCacheCreator.buildDataSource.update.config.fail,e=", e);
//            }
//        }
//    }
//
//    private static Object convertValue(String data, Class<?> type) {
//        Object value = null;
//        try {
//            Method method = type.getDeclaredMethod("valueOf", String.class);
//            boolean accessible = method.isAccessible();
//            method.setAccessible(true);
//            value = method.invoke(null, data);
//            method.setAccessible(accessible);
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            log.error("DataSourceCacheCreator.buildDataSource.covert.value.fail,e=", e);
//        }
//        return value;
//    }
//
//    private static Map<String, Object> convertProperties(Map<String, Object> source, Filter filter) {
//        Map<String, Object> res = new HashMap<>(source.size());
//        for (Map.Entry<String, Object> property : source.entrySet()) {
//            if (filter.check(property.getKey())) {
//                String key = property.getKey();
//                int point = key.indexOf(DataSourceConstant.DS_POINT);
//                if (point > 0) {
//                    key = key.substring(point + 1);
//                }
//                key = toCaml(key);
//                res.put(key, property.getValue());
//            }
//        }
//        return res;
//    }
//
//    private static String toCaml(String key) {
//        if (StringUtils.isEmpty(key)) {
//            return key;
//        }
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < key.length(); i++) {
//            if (key.charAt(i) == DataSourceConstant.DS_CAML) {
//                if (i < key.length() - 1) {
//                    i++;
//                    builder.append(Character.toUpperCase(key.charAt(i)));
//                }
//            } else {
//                builder.append(key.charAt(i));
//            }
//        }
//        return builder.toString();
//    }
//
//    @FunctionalInterface
//    private interface Filter {
//        /**
//         * 检查key是否符合条件
//         *
//         * @param key key
//         * @return 是否符合
//         */
//        boolean check(String key);
//    }
//
//
//    @SuppressWarnings("unchecked")
//    private static Map<String, Object> getProperties(Environment environment, String prefix) {
//        Map<String, Object> propertyMap = new HashMap<>(16);
//        if (StringUtils.isEmpty(prefix)) {
//            return propertyMap;
//        }
//        if (environment instanceof StandardEnvironment) {
//            StandardEnvironment standardEnvironment = (StandardEnvironment) environment;
//            for (PropertySource<?> source : standardEnvironment.getPropertySources()) {
//                Object objectSource = source.getSource();
//                if (objectSource instanceof Map) {
//                    Map<String, Object> properties = (Map<String, Object>) objectSource;
//                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
//                        if (entry.getKey().startsWith(prefix)) {
//                            String key = entry.getKey().substring(prefix.length());
//                            propertyMap.put(key, entry.getValue());
//                        }
//                    }
//                }
//            }
//        }
//        return propertyMap;
//    }
//
//
//    public static String decryptPass(String key, String pass) {
//        if (!StringUtils.isEmpty(key)) {
//            //解密密码
//            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
//            textEncryptor.setPassword(key);
//            try {
//                pass = textEncryptor.decrypt(pass);
//                log.debug("DataSourceCacheCreator.buildDataSource.pass.decrypt.success!");
//            } catch (Exception e) {
//                log.debug("DataSourceCacheCreator.buildDataSource.pass.decrypt.fail!");
//            }
//        } else {
//            log.debug("DataSourceCacheCreator.buildDataSource.encryptor.key.not.find!");
//        }
//        return pass;
//    }
//
//    private static int checkAndUpdateDataSource(String url, String userName, String pass, boolean schema, DataSourceProperty property) {
//        Connection connection = null;
//        int re = -1;
//        try {
//            Properties info = new Properties();
//            info.put("user", userName);
//            info.put("password", pass);
//            info.put("loginTimeout", "3");
//            connection = DriverManager.getConnection(url, info);
//            if (schema) {
//                url = addSchema(url, connection.getSchema());
//            }
//            property.setUrl(url);
//            property.setPassword(pass);
//            re = 0;
//        } catch (SQLException e) {
//            String db = getDb(url);
//            log.debug("DataSourceCacheCreator.checkDataSource.connect.fail,db={},user={},url={}", db, userName, property.getUrl());
//
//            if (e.getMessage() != null && e.getMessage().contains(DataSourceConstant.AUTHENTICATION)) {
//                re = 1;
//                DbCreateErrorContext.error("连接失败：[url=", url, "],[user=", userName, "],error=密码错误");
//            } else {
//                log.error("DataSourceCacheCreator.checkDataSource.connect.fail,db={},user={},e={}", db, userName, e);
//                DbCreateErrorContext.error("连接失败：[url=", url, "],[user=", userName, "],error=", e.getMessage());
//            }
//
//        } finally {
//            JdbcUtils.close(connection);
//        }
//        return re;
//    }
//
//    public static boolean checkDataSource(DataSourceProperty property) {
//        Connection connection = null;
//        try {
//            Properties info = new Properties();
//            info.put("user", property.getUsername());
//            info.put("password", property.getPassword());
//            info.put("loginTimeout", "3");
//            connection = DriverManager.getConnection(property.getUrl(), info);
//            return true;
//        } catch (SQLException e) {
//            String db = getDb(property.getUrl());
//            log.error("DataSourceCacheCreator.checkDataSource.connect.fail,db={},user={},e={}", db, property.getUsername(), e);
//            return false;
//        } finally {
//            JdbcUtils.close(connection);
//        }
//    }
//
//    /**
//     * 更新数据库连接url
//     *
//     * @param url  当前url
//     * @param addr 目标数据库地址
//     * @return 新url
//     */
//    private static String updateUrl(String url, String addr) {
//        if (StringUtils.isEmpty(addr)) {
//            return url;
//        }
//        if (!addr.contains(DataSourceConstant.DS_PORT)) {
//            addr += DataSourceConstant.DS_PORT + DataSourceConstant.DEFAULT_PORT;
//            log.debug("DataSourceCacheCreator.updateUrl.addr.unComplete.miss.port,addr={},backPort={}", addr, DataSourceConstant.DEFAULT_PORT);
//        }
//        return setUrlMark(url, addr);
//    }
//
//    private static String setUrlMark(String url, String mark) {
//        int start = url.indexOf(DataSourceConstant.DS_DOMAIN_START) + DataSourceConstant.DS_DOMAIN_START.length();
//        int end = url.indexOf(DataSourceConstant.DS_DOMAIN_END, start);
//        return url.substring(0, start) + mark + url.substring(end);
//    }
//
//
//    /**
//     * 添加currentSchema到url中
//     *
//     * @param url    连接url
//     * @param schema schema
//     * @return 更新后的url
//     */
//    private static String addSchema(String url, String schema) {
//
//        if (StringUtils.isEmpty(schema)) {
//            log.error("DataSourceCacheCreator.dataSource.schema.not.exists,url={}", url);
//            return url;
//        }
//        //包含schema，无需添加
//        if (url.contains(DataSourceConstant.DS_SCHEMA)) {
//            log.debug("DataSourceCacheCreator.dataSource.schema.exists.in.url,schema={},url={}", schema, url);
//            return url;
//        }
//        //添加schema信息
//        int point = url.indexOf(DataSourceConstant.DS_QUESTION);
//        if (point > 0) {
//            url = url.substring(0, point + 1) + DataSourceConstant.DS_SCHEMA + schema + DataSourceConstant.DS_SPLIT_OF_PARAMETER + url.substring(point + 1);
//            log.debug("DataSourceCacheCreator.dataSource.add.schema.to.url,schema={},url={}", schema, url);
//            return url;
//        }
//        return url;
//    }
//
//    private static boolean checkSchema(String user, String list) {
//        if (StringUtils.isEmpty(list)) {
//            return true;
//        }
//        if (list.contains(DataSourceConstant.SCHEMA_ALL)) {
//            return false;
//        }
//        String[] datas = list.split(DataSourceConstant.COMMA_SPLIT);
//        for (String data : datas) {
//            if (user.equals(data)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static final String BASE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_+";
//    public static final int LENGTH = 24;
//    public static final String SALT_A = "kun-pass";
//    public static final String SALT_B = "second-salt";
//
//    public static String buildPass(String key, String dbKey, String userName) {
//        log.debug("DataSourceCacheCreator.buildPass.build.with.user={}", userName);
//        String info = DigestUtils.md5DigestAsHex((SALT_A + dbKey + userName).getBytes(StandardCharsets.UTF_8));
//        info = DigestUtils.md5DigestAsHex((SALT_B + info + key + dbKey).getBytes(StandardCharsets.UTF_8));
//        StringBuilder res = new StringBuilder();
//        long seedA;
//        long seedB;
//        seedA = Long.parseLong(info.substring(0, 15), 16);
//        seedB = Long.parseLong(info.substring(15, 30), 16);
//        RandomBuild a = new RandomBuild(seedA);
//        RandomBuild b = new RandomBuild(seedB);
//        for (int i = 0; i < LENGTH; i++) {
//            RandomBuild random;
//            if (i % 2 == 0) {
//                random = a;
//            } else {
//                random = b;
//            }
//            int range = BASE.length();
//            int offset = random.nextInt(range);
//            char data = BASE.charAt(offset);
//            res.append(data);
//        }
//        return res.toString();
//    }
//
//    private static class RandomBuild {
//        public static final int INT_BITS = 31;
//        private long seed;
//
//        private static final long MULTIPLIER = 0x5DEECE66DL;
//        private static final long ADDEND = 0xBL;
//        public static final int COUNT = 48;
//        private static final long MASK = (1L << COUNT) - 1;
//
//        public RandomBuild(long seed) {
//            this.seed = seed;
//        }
//
//        private int next() {
//            seed = (seed * MULTIPLIER + ADDEND) & MASK;
//            return (int) (seed >>> (COUNT - INT_BITS));
//        }
//
//        public int nextInt(int bound) {
//            int r = next();
//            int m = bound - 1;
//            if ((bound & m) == 0)  // i.e., bound is a power of 2
//            {
//                r = (int) ((bound * (long) r) >> INT_BITS);
//            } else {
//                int u = r;
//                while (u - (r = u % bound) + m < 0) {
//                    u = next();
//                }
//            }
//            return r;
//        }
//    }
//
//
//    ApplicationEventPublisher applicationEventPublisher;
//
//    @Override
//    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//
//}
