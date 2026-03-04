//package io.hankun.framework.db.auth;
//
//import com.alibaba.cloud.nacos.NacosConfigManager;
//import com.alibaba.nacos.api.exception.NacosException;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class DbAuthConfigLoader {
//
//    public static final String CONFIG_ID = "config-common-db-auth.properties";
//
//    public static final String GROUP = "DEFAULT_GROUP";
//
//    @Autowired(required = false)
//    private NacosConfigManager nacosConfigManager;
//
//    @Getter
//    private boolean enable = false;
//
//    private Map<String, String> configMap = null;
//
//    @Value("${spring.application.name}")
//    @Getter
//    private String applicationName;
//
//    public String getConfig(String serverName) {
//        Map<String, String> configs = configMap;
//        if (CollectionUtils.isEmpty(configs)) {
//            return null;
//        }
//
//        for (Map.Entry<String, String> config : configs.entrySet()) {
//            if (serverName.startsWith(config.getKey())) {
//                return config.getValue();
//            }
//        }
//
//        return null;
//    }
//
//    @PostConstruct
//    public boolean init() {
//        String configs = getConfigList();
//        if (StringUtils.isEmpty(configs)) {
//            return true;
//        }
//        Map<String, String> configResult = new HashMap<>();
//        String[] configList = configs.split("\\r?\\n");
//        for (String config : configList) {
//            if (config.startsWith(AuthCheckService.KUN_DB_AUTH_CONFIG)) {
//                int index = config.indexOf("=");
//                String serviceName = config.substring(AuthCheckService.KUN_DB_AUTH_CONFIG.length(), index);
//                String dbAuthConfig = config.substring(index + 1);
//                if (getApplicationName().startsWith(serviceName)) {
//                    configResult.put(serviceName, dbAuthConfig);
//                }
//            } else if (config.startsWith(AuthCheckService.KUN_DB_AUTH_ENABLE)) {
//                String enableStr = config.substring(config.indexOf("=") + 1);
//                enable = Boolean.parseBoolean(enableStr);
//            }
//        }
//        configMap = configResult;
//        return false;
//    }
//
//    protected String getConfigList() {
//        if (nacosConfigManager == null) {
//            return null;
//        }
//        try {
//            return nacosConfigManager.getConfigService().getConfig(CONFIG_ID, GROUP, 3000);
//        } catch (NacosException e) {
//            log.info("DataSourceDestroyListener.read.nacos.failed,e=", e);
//            return null;
//        }
//    }
//}
