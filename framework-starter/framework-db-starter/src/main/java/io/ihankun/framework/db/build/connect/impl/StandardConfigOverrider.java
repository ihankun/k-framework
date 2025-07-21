package io.ihankun.framework.db.build.connect.impl;


import io.ihankun.framework.db.build.BuildInfoHolder;
import io.ihankun.framework.db.build.connect.ConfigOverriderFilter;
import io.ihankun.framework.db.build.connect.config.DsOverrideConfig;
import io.ihankun.framework.db.build.connect.entity.ConfigTargetType;
import io.ihankun.framework.db.build.connect.entity.ConnectionConfig;
import io.ihankun.framework.db.build.connect.entity.DsOverriderConfig;
import io.ihankun.framework.db.build.ds.DataSourceConfig;
import io.ihankun.framework.db.build.entity.ConfigOverrideGroup;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

/**
 * @author hankun
 */
public class StandardConfigOverrider implements ConfigOverriderFilter {

    private final DsOverrideConfig dsOverrideConfig;

    private final ConfigOverrideGroup group;

    public StandardConfigOverrider(DsOverrideConfig kunDsOverrideConfig, ConfigOverrideGroup group) {
        this.dsOverrideConfig = kunDsOverrideConfig;
        this.group = group;
    }

    public List<DsOverriderConfig> getConfigList(@NotNull DsOverrideConfig config) {
        return group.getConfigList(config);
    }

    @Override
    public String name() {
        return "stand-" + group.getCode();
    }

    @Override
    public int order() {
        return 10 + group.ordinal();
    }

    private List<DsOverriderConfig> getConfigListInner() {
        if (dsOverrideConfig == null) {
            return Collections.emptyList();
        }
        List<DsOverriderConfig> result = getConfigList(dsOverrideConfig);
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public void before(DataSourceConfig dataSourceConfig, BuildInfoHolder buildInfoHolder) {
        for (DsOverriderConfig overriderConfig : getConfigListInner()) {
            if (overriderConfig.getAfterConnectSuc() != null && overriderConfig.getAfterConnectSuc()) {
                continue;
            }
            ConfigTargetType type = ConfigTargetType.getByCode(overriderConfig.getType());
            overrideConfig(dataSourceConfig, group, type, overriderConfig);
        }
    }

    @Override
    public void after(DataSourceConfig dataSourceConfig, ConnectionConfig connectionConfig,
                      BuildInfoHolder buildInfoHolder, Connection connection) {
        for (DsOverriderConfig overriderConfig : getConfigListInner()) {
            if (overriderConfig.getAfterConnectSuc() != null && overriderConfig.getAfterConnectSuc()) {
                ConfigTargetType type = ConfigTargetType.getByCode(overriderConfig.getType());
                overrideConfig(dataSourceConfig, group, type, overriderConfig);
            }
        }
    }


}
