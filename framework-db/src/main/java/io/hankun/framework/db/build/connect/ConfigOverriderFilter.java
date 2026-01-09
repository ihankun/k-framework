package io.hankun.framework.db.build.connect;

import io.hankun.framework.db.build.BuildInfoHolder;
import io.hankun.framework.db.build.connect.entity.ConfigTargetType;
import io.hankun.framework.db.build.connect.entity.ConnectionConfig;
import io.hankun.framework.db.build.connect.entity.DsOverriderConfig;
import io.hankun.framework.db.build.ds.DataSourceConfig;
import io.hankun.framework.db.build.entity.ConfigOverrideGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author hankun
 */
public interface ConfigOverriderFilter {

    Logger log = LoggerFactory.getLogger(ConfigOverriderFilter.class);

    String name();

    int order();

    void before(DataSourceConfig dataSourceConfig, BuildInfoHolder buildInfoHolder);

    void after(DataSourceConfig dataSourceConfig,
               ConnectionConfig connectionConfig, BuildInfoHolder buildInfoHolder, Connection connection) throws SQLException;


    default void overrideConfig(DataSourceConfig dataSourceConfig, ConfigOverrideGroup group, ConfigTargetType type,
                                DsOverriderConfig overriderConfig) {
        if (type == null) {
            return;
        }
        String config = type.getTarget(dataSourceConfig);
        if (config == null) {
            config = "";
        }
        type.setTarget(dataSourceConfig, updateConfig(dataSourceConfig, group, type, config, overriderConfig));
    }

    default String updateConfig(DataSourceConfig dataSourceConfig, ConfigOverrideGroup group, ConfigTargetType type,
                                String configData, DsOverriderConfig overriderConfig) {
        if (group.matchGroup(dataSourceConfig, overriderConfig.getGroup())) {
            String result = configData.replaceAll(overriderConfig.getSource(), overriderConfig.getTarget());
            if (!Objects.equals(configData, result)) {
                log.info("配置替换成功, 成功将【{}:{}】【{}】的配置【{}】替换为【{}】,源ds={}", group.getDesc(),
                        overriderConfig.getGroup(), type.getDesc(), configData, result, dataSourceConfig);
            } else {
                log.info("配置匹配，但是未替换【{}:{}】【{}】的配置【{}】,源ds={}", group.getDesc(),
                        overriderConfig.getGroup(), type.getDesc(), configData, dataSourceConfig);
            }
            return result;
        }
        return configData;
    }
}
