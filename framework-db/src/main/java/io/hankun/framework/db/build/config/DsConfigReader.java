package io.hankun.framework.db.build.config;


import io.hankun.framework.db.build.ds.DataSourceConfig;
import io.hankun.framework.db.build.entity.PassGenerateType;
import io.hankun.framework.db.config.DataSourceConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hankun
 */
@Slf4j
public class DsConfigReader {

    private static final String HIS_MASTER = ".HIS_MASTER";

    public static final String DEFAULT = "default";

    public static final String HIS_SLAVE = "HIS_SLAVE";

    public static final String HIS_SLAVE_SLAVE = "HIS_SLAVE_SLAVE";

    private final DsConfig dsConfig;

    private final Environment environment;

    private final String envKey;

    public DsConfigReader(DsConfig dsConfig, Environment environment) {
        this.dsConfig = dsConfig;
        this.environment = environment;
        this.envKey = environment.getProperty(DataSourceConstant.PUBLIC_KEY);
    }

    public String getAddress(DataSourceConfig dataSourceConfig) {
        return getConfig(dataSourceConfig.getDomain(), dataSourceConfig.getDbMark());
    }

    public String getPass(DataSourceConfig dataSourceConfig) {
        return getConfig(dataSourceConfig.getDomain(), dataSourceConfig.getUser());
    }

    public String getSpecialPass(DataSourceConfig dataSourceConfig) {
        return getConfig(dataSourceConfig.getDomain(), dataSourceConfig.getUser() +
                DataSourceConstant.DS_POINT + dataSourceConfig.getDbMark());
    }

    public String getEnvKey(DataSourceConfig dataSourceConfig) {

        //先尝试从Nacos中获取该域名的key
        String privateKey = environment.getProperty(DataSourceConstant.NEW_PRIVATE_KEY + dataSourceConfig.getDomain());
        Boolean disableEnvKey = dsConfig.getDisableEnvKey();
        String key;
        if (privateKey != null) {
            key = privateKey;
        } else {
            //从环境变量中获取该环境的key
            key = envKey;
            if (disableEnvKey != null && disableEnvKey) {
                key = "";
            }
        }
        if (StringUtils.isEmpty(key)) {
            key = "";
        }
        return key;
    }

    public String getDomainKey(DataSourceConfig dataSourceConfig) {
        return dsConfig.getKey().get(dataSourceConfig.getDomain());
    }

    public String getReRouteDomain(String domain) {
        boolean reRouteEnable = dsConfig.getRerouteEnable();
        if (reRouteEnable) {
            String reroute = dsConfig.getReroute().get(domain);
            if (StringUtils.hasText(reroute)) {
                log.debug("域名重定向成功,原始域名={},重定向数据源的域名={}", domain, reroute);
                return reroute;
            }
        }
        return domain;
    }

    public PassGenerateType getPassGenerateType(DataSourceConfig dataSourceConfig) {
        String type = dsConfig.getPassGenerateType().get(dataSourceConfig.getDomain());
        return PassGenerateType.getByCode(type);
    }

    public String getPassGenerateInfo(DataSourceConfig dataSourceConfig) {
        return dsConfig.getPassGenerateInfo().get(dataSourceConfig.getDomain());
    }

    public String getDefDomain() {
        return dsConfig.getDefaultDomain();
    }

    public List<String> getAllDomain() {
        List<String> allDomain = new ArrayList<>();
        String defaultDomain = getDefDomain();
        if (StringUtils.hasText(defaultDomain)) {
            allDomain.add(defaultDomain);
        }
        for (String key : dsConfig.getDb().keySet()) {
            if (!key.endsWith(HIS_MASTER)) {
                continue;
            }
            String domain = key.substring(0, key.length() - HIS_MASTER.length());
            if (allDomain.contains(domain)) {
                continue;
            }
            allDomain.add(domain);
        }
        for (String key : dsConfig.getKey().keySet()) {
            if (allDomain.contains(key)) {
                continue;
            }
            allDomain.add(key);
        }
        return allDomain;
    }

    public String[] getSeataDisableList() {
        String disableList = dsConfig.getSeataDisableList();
        String[] disables;
        if (!org.apache.commons.lang3.StringUtils.isEmpty(disableList)) {
            disables = disableList.split(",");
        } else {
            disables = new String[]{HIS_SLAVE, HIS_SLAVE_SLAVE};
        }
        return disables;
    }

    public String getCurrentService() {
        return dsConfig.getCurrentService();
    }

    public String getUrlSuffix() {
        return dsConfig.getUrlSuffix();
    }

    private String getConfig(String domain, String key) {
        return dsConfig.getDb().get(domain + DataSourceConstant.DS_POINT + key);
    }

    public Boolean getLazyLoad() {
        if (dsConfig.getLazyLoad() == null) {
            return false;
        }
        return dsConfig.getLazyLoad();
    }

    public Boolean getStopWhenInitFailed() {
        if (dsConfig.getStopWhenInitFailed() == null) {
            return false;
        }
        return dsConfig.getStopWhenInitFailed();
    }

    public Boolean getLoadOldConfig() {
        if (dsConfig.getLoadOldConfig() == null) {
            if (dsConfig.getIsProperties() != null) {
                return !dsConfig.getIsProperties();
            }
            return true;
        }
        return dsConfig.getLoadOldConfig();
    }

}
