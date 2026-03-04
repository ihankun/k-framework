package io.hankun.framework.db.build;


import io.hankun.framework.db.build.config.ChConfig;
import io.hankun.framework.db.build.config.DsConfig;
import io.hankun.framework.db.build.config.DsConfigReader;
import io.hankun.framework.db.build.ds.DataSourceConfig;
import io.hankun.framework.db.config.DataSourceConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hankun
 */
@Slf4j
public class DsPropertiesProvider {

    private final Map<String, DataSourceConfig> dataSourceConfigMap = new ConcurrentHashMap<>();

    private final ChConfig chConfig;

    private final DsConfigReader kunDsConfigReader;


    public DsPropertiesProvider(DsConfig kunDsConfig,
                                ChConfig kunChConfig,
                                DsConfigReader kunDsConfigReader) {
        this.chConfig = kunChConfig;
        this.kunDsConfigReader = kunDsConfigReader;
        if (!CollectionUtils.isEmpty(kunDsConfig.getDatasource())) {
            for (Map.Entry<String, String> entry : kunDsConfig.getDatasource().entrySet()) {
                DataSourceConfig dataSourceConfig = build(entry.getKey(), entry.getValue(), kunDsConfig.getCurrentService());
                if (dataSourceConfig != null) {
                    initDs(dataSourceConfig);
                    dataSourceConfigMap.put(entry.getKey(), dataSourceConfig);
                }
            }
        }
        if (!CollectionUtils.isEmpty(kunChConfig.getDatasource())) {
            for (Map.Entry<String, String> entry : kunChConfig.getDatasource().entrySet()) {
                DataSourceConfig dataSourceConfig = build(entry.getKey(), entry.getValue(), kunDsConfig.getCurrentService());
                if (dataSourceConfig != null) {
                    initCh(dataSourceConfig);
                    dataSourceConfigMap.put(entry.getKey(), dataSourceConfig);
                }
            }
        }
    }

    /**
     * 动态添加数据源配置，会进行配置复制，不会反向影响参数，会覆盖已有配置
     *
     * @param dataSourceConfig 数据源配置
     */
    public void addDsConfig(DataSourceConfig dataSourceConfig) {
        dataSourceConfigMap.compute(dataSourceConfig.getDs(), (ds, value) -> {
            DataSourceConfig config = new DataSourceConfig();
            BeanUtils.copyProperties(dataSourceConfig, config);
            config.setDomain("");
            config.setServiceName(kunDsConfigReader.getCurrentService());
            return config;
        });
    }


    private DataSourceConfig build(String key, String config, String serviceName) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDomain("");
        dataSourceConfig.setServiceName(serviceName);
        dataSourceConfig.setDs(key);
        dataSourceConfig.setSchema("");
        if (buildConfig(key, config, dataSourceConfig)) {
            String[] seataDisableList = kunDsConfigReader.getSeataDisableList();
            for (String disable : seataDisableList) {
                disable = disable.trim();
                if (disable.equals(dataSourceConfig.getDbMark())) {
                    dataSourceConfig.setSeata(false);
                }
            }
            return dataSourceConfig;
        }
        return null;
    }

    public void initDs(DataSourceConfig config) {
        config.setProtocol("jdbc:postgresql");
        String urlSuffix = kunDsConfigReader.getUrlSuffix();
        if (StringUtils.isEmpty(urlSuffix)) {
            urlSuffix = DsConfig.URL_SUFFIX;
        }
        config.setParameter(urlSuffix);
        config.setDriver("org.postgresql.Driver");
    }

    public void initCh(DataSourceConfig config) {
        config.setProtocol("jdbc:clickhouse");
        String urlSuffix = chConfig.getUrlSuffix();
        if (StringUtils.isEmpty(urlSuffix)) {
            urlSuffix = ChConfig.CH_URL_SUFFIX;
        }
        config.setParameter(urlSuffix);
        config.setDriver("com.clickhouse.jdbc.ClickHouseDriver");
    }

    private boolean buildConfig(String key, String config, DataSourceConfig dataSourceConfig) {
        int pointA = config.indexOf(DataSourceConstant.DS_DATA_SPLIT);
        int pointB = config.indexOf(DataSourceConstant.DS_POINT);
        if (pointA < 0 || pointB < 0) {
            log.error("kunDsPropertiesProvider.buildConfig.wrong.config,info={},value={}", key, config);
            return false;
        }

        String address = config.substring(0, pointA);
        String db = config.substring(pointA + 1, pointB);
        String user = config.substring(pointB + 1);
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(db) || StringUtils.isEmpty(user)) {
            log.error("kunDsPropertiesProvider.buildConfig.config.error,info={},value={}", key, config);
            return false;
        }
        dataSourceConfig.setDbMark(address);
        dataSourceConfig.setDb(db);
        dataSourceConfig.setUser(user);
        return true;
    }


    public Set<String> listGroup() {
        return dataSourceConfigMap.keySet();
    }

    public Collection<DataSourceConfig> listGroupConfig() {
        return dataSourceConfigMap.values();
    }

    /**
     * 获取数据源配置模板，返回的是复制后的对象，可修改
     *
     * @return DataSourceConfig
     */
    public DataSourceConfig getDataSourceConfig(String ds) {
        if (ds == null) {
            return null;
        }
        DataSourceConfig config = dataSourceConfigMap.get(ds);
        if (config == null) {
            return null;
        }
        DataSourceConfig copy = new DataSourceConfig();
        BeanUtils.copyProperties(config, copy);
        return copy;
    }

    public List<String> getDsByDbMark(String dbMark) {
        List<String> dsList = new ArrayList<>();
        for (Map.Entry<String, DataSourceConfig> entry : dataSourceConfigMap.entrySet()) {
            if (Objects.equals(dbMark, entry.getValue().getDbMark())) {
                dsList.add(entry.getKey());
            }
        }
        return dsList;
    }
}
