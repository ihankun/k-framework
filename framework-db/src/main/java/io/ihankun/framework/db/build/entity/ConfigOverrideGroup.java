package io.ihankun.framework.db.build.entity;

import io.ihankun.framework.db.build.connect.config.DsOverrideConfig;
import io.ihankun.framework.db.build.connect.entity.DsOverriderConfig;
import io.ihankun.framework.db.build.druid.KDruidConfig;
import io.ihankun.framework.db.build.druid.config.DsDruidConfig;
import io.ihankun.framework.db.build.ds.DataSourceConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum ConfigOverrideGroup {

    ALL("all", "全部") {
        @Override
        public String getGroupCode(DataSourceConfig dataSourceConfig) {
            return "";
        }

        @Override
        public List<DsOverriderConfig> getConfigList(DsOverrideConfig config) {
            return config.getAll();
        }

        @Override
        public List<KDruidConfig> getDruidConfigList(DsDruidConfig config) {
            return config.getAll();
        }

        @Override
        public boolean matchGroup(DataSourceConfig dataSourceConfig, String groups) {
            return true;
        }
    },

    DOMAIN("domain", "域名") {
        @Override
        public String getGroupCode(DataSourceConfig dataSourceConfig) {
            return dataSourceConfig.getDomain();
        }

        @Override
        public List<DsOverriderConfig> getConfigList(DsOverrideConfig config) {
            return config.getDomains();
        }

        @Override
        public List<KDruidConfig> getDruidConfigList(DsDruidConfig config) {
            return config.getDomains();
        }
    },

    SERVICE("service", "服务") {
        @Override
        public String getGroupCode(DataSourceConfig dataSourceConfig) {
            return dataSourceConfig.getServiceName();
        }

        @Override
        public List<DsOverriderConfig> getConfigList(DsOverrideConfig config) {
            return config.getServices();
        }

        @Override
        public List<KDruidConfig> getDruidConfigList(DsDruidConfig config) {
            return config.getServices();
        }
    },

    SD("sd", "服务+域名") {
        @Override
        public String getGroupCode(DataSourceConfig dataSourceConfig) {
            return dataSourceConfig.getServiceName() + "@" + dataSourceConfig.getDomain();
        }

        @Override
        public List<DsOverriderConfig> getConfigList(DsOverrideConfig config) {
            return config.getSds();
        }

        @Override
        public List<KDruidConfig> getDruidConfigList(DsDruidConfig config) {
            return config.getSds();
        }
    },

    SU("su", "服务+用户名") {
        @Override
        public String getGroupCode(DataSourceConfig dataSourceConfig) {
            return dataSourceConfig.getServiceName() + "@" + dataSourceConfig.getUser();
        }

        @Override
        public List<DsOverriderConfig> getConfigList(DsOverrideConfig config) {
            return config.getSus();
        }

        public List<KDruidConfig> getDruidConfigList(DsDruidConfig config) {
            return config.getSus();
        }
    };

    private final String code;
    private final String desc;

    public abstract String getGroupCode(DataSourceConfig dataSourceConfig);

    public abstract List<DsOverriderConfig> getConfigList(DsOverrideConfig config);

    public abstract List<KDruidConfig> getDruidConfigList(DsDruidConfig config);


    public boolean matchGroup(DataSourceConfig dataSourceConfig, String groups) {
        if (StringUtils.isEmpty(groups)) {
            return false;
        }
        String group = getGroupCode(dataSourceConfig);
        String[] targets = groups.split(",");
        for (String t : targets) {
            if (t.equals(group)) {
                return true;
            }
        }
        return false;
    }
}
