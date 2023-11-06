package io.ihankun.framework.spring.server.nacos.properties;//package com.ihankun.core.spring.server.nacos.properties;
//
//import com.alibaba.cloud.nacos.NacosConfigManager;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.*;
//
//@Slf4j
//@ConditionalOnClass(NacosConfigManager.class)
//@Component
//public class DatasetDbUserInfoHolder {
//
//    public static final String NEW_PRIVATE_KEY = "msun.ds.private.key.";
//    public static final String PUBLIC_KEY = "MSUN_DS_PUBLIC_KEY";
//    private static final String DS_PREFIX = "msun.ds.db.";
//    private static final String DS_POINT = ".";
//
//    private static final String CDR_READ_USER = "cdrappread";
//
//    private static final String CDR_USER = "cdrapp";
//
//    private static final String CDR_IN_HIS_USER = "cdrapp_read";
//
//    private static final String HIS_SLAVE_SLAVE = "HIS_SLAVE_SLAVE";
//
//    private static final String DATASET = "DATASET";
//    public static final String CDRAPP = "cdrapp";
//
//    public static final String DB_BUILD_PRE = "msun.ds.key.";
//
//    public static final String USE_ENV_KEY = "msun.ds.disable.env.key";
//    public static final String CHIS = "chis";
//    public static final String CHISAPP = "chisapp";
//    public static final String HIS_MASTER = "HIS_MASTER";
//    public static final String HIS_SLAVE = "HIS_SLAVE";
//
//    @Getter
//    private static class IpInfo {
//        public static final int TWO = 2;
//        public static final int DEFAULT_PORT = 5432;
//
//        private final String addr;
//        private final Integer port;
//
//        public IpInfo(String info) {
//            String[] infos = info.split(":");
//            String addr = infos[0];
//            int port = DEFAULT_PORT;
//            if (infos.length == TWO) {
//                try {
//                    port = Integer.parseInt(infos[1]);
//                } catch (NumberFormatException e) {
//                    log.info("DatasetDbUserInfoHolder.IpInfo.decode.ip.fail,addr={}", info);
//                }
//            }
//            this.addr = addr;
//            this.port = port;
//        }
//
//        public IpInfo(String addr, Integer port) {
//            this.addr = addr;
//            this.port = port;
//        }
//
//        public static IpInfo EMPTY = new IpInfo(null, 0);
//    }
//
//    @Resource
//    private Environment environment;
//
//    @Autowired(required = false)
//    private NacosConfigManager nacosConfigManager;
//
//    private String nameSpaceName;
//
//    private Method decryptPass;
//
//    private Method buildPass;
//
//    @PostConstruct
//    private void init() {
//        Class<?> clazz = null;
//        try {
//            clazz = Class.forName("com.msun.core.db.dynamic.DataSourceCacheCreator");
//        } catch (ClassNotFoundException e) {
//            log.error("DatasetDbUserInfoHolder.init.load.DataSourceCacheCreator.fail,e={}", e.getMessage());
//        }
//        if (clazz == null) {
//            return;
//        }
//        try {
//            decryptPass = clazz.getMethod("decryptPass", String.class, String.class);
//            decryptPass.setAccessible(true);
//        } catch (NoSuchMethodException e) {
//            log.error("DatasetDbUserInfoHolder.init.load.DataSourceCacheCreator.decryptPass.fail,e={}", e.getMessage());
//        }
//        try {
//            buildPass = clazz.getMethod("buildPass", String.class, String.class, String.class);
//            buildPass.setAccessible(true);
//        } catch (NoSuchMethodException e) {
//            log.error("DatasetDbUserInfoHolder.init.load.DataSourceCacheCreator.buildPass.fail,e={}", e.getMessage());
//        }
//    }
//
//    public DatasetDbUserInfo.DatabaseDTO getDatabaseInfo(String user, String dbKey, String pre, String dbName, IpInfo addr, String domain) {
//        DatasetDbUserInfo.DatabaseDTO databaseDTO = new DatasetDbUserInfo.DatabaseDTO();
//        databaseDTO.setDbName(dbName);
//        databaseDTO.setUrl(addr.getAddr());
//        databaseDTO.setPort(addr.getPort());
//        databaseDTO.setUserName(user);
//        String pass = environment.getProperty(pre + user);
//
//        String key;
//        //先尝试从Nacos中获取该域名的key
//        boolean nDomainKeyFlag = environment.containsProperty(NEW_PRIVATE_KEY + domain);
//        if (nDomainKeyFlag) {
//            key = environment.getProperty(NEW_PRIVATE_KEY + domain);
//        } else {
//            //获取该环境的环境变量的key
//            key = environment.getProperty(PUBLIC_KEY);
//        }
//
//        if (StringUtils.isEmpty(pass)) {
//            if (!StringUtils.isEmpty(dbKey)) {
//                if (! nDomainKeyFlag) {
//                    String useEnv = environment.getProperty(USE_ENV_KEY);
//                    if (key == null) {
//                        key = "";
//                    }
//                    if (Boolean.TRUE.toString().equals(useEnv)) {
//                        key = "";
//                    }
//                    if (StringUtils.isEmpty(key)) {
//                        log.info("DatasetDbUserInfoHolder.getDatabaseInfo.env.key.not.exists");
//                    }
//                }
//                pass = buildPass(key, dbKey, user);
//            }
//        } else {
//            if (!StringUtils.isEmpty(key)) {
//                pass = decryptPass(key, pass);
//            }
//        }
//        databaseDTO.setPassword(pass);
//        return databaseDTO;
//    }
//
//    public String getNameSpace() {
//        if (nameSpaceName != null) {
//            return nameSpaceName;
//        }
//        synchronized (this) {
//            if (nameSpaceName != null) {
//                return nameSpaceName;
//            }
//            nameSpaceName = geNameSpaceDirectly();
//        }
//        return nameSpaceName;
//    }
//
//    private String geNameSpaceDirectly() {
//        if (nacosConfigManager == null) {
//            return "nacos加载错误";
//        }
//        String namespaceName = "获取失败";
//        try {
//            String addr = "http://" + nacosConfigManager.getNacosConfigProperties().getServerAddr() + "/nacos/v1/console/namespaces";
//            HttpRestResult<NacosNamespaceInfo> httpResult = NamingHttpClientManager.getInstance().getNacosRestTemplate().get(addr, Header.EMPTY, Query.EMPTY, NacosNamespaceInfo.class);
//            Optional<NacosNamespaceInfo.DataDTO> dataDTO = httpResult.getData().getData().stream().filter(data -> {
//                if (data.getNamespace() == null) {
//                    return false;
//                }
//                return data.getNamespace().equals(nacosConfigManager.getNacosConfigProperties().getNamespace());
//            }).findFirst();
//            if (dataDTO.isPresent()) {
//                namespaceName = dataDTO.get().getNamespaceShowName();
//            }
//            return namespaceName;
//        } catch (Exception e) {
//            return namespaceName;
//        }
//    }
//
//    public String decryptPass(String key, String pass) {
//        if (decryptPass == null) {
//            log.error("DatasetDbUserInfoHolder.decryptPass.fail,load fail");
//            return null;
//        }
//        try {
//            return decryptPass.invoke(null, key, pass).toString();
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            log.error("DatasetDbUserInfoHolder.decryptPass.fail,e={}", e.getMessage());
//            return null;
//        }
//    }
//
//    public String buildPass(String key, String dbKey, String userName) {
//        if (buildPass == null) {
//            log.error("DatasetDbUserInfoHolder.buildPass.fail,load fail");
//            return null;
//        }
//        try {
//            return buildPass.invoke(null, key, dbKey, userName).toString();
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            log.error("DatasetDbUserInfoHolder.buildPass.fail,e={}", e.getMessage());
//            return null;
//        }
//    }
//}
