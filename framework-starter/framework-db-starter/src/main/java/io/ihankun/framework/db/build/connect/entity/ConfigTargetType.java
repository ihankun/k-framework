package io.ihankun.framework.db.build.connect.entity;


import io.ihankun.framework.db.build.ds.DataSourceConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum ConfigTargetType {
    //ds、db、user、schema、protocol、parameter、driver

    DS("ds", "数据源") {
        @Override
        public String getTarget(DataSourceConfig config) {
            return config.getDbMark();
        }

        @Override
        public void setTarget(DataSourceConfig config, String target) {
            config.setDbMark(target);
        }
    },
    DB("db", "数据库") {
        @Override
        public String getTarget(DataSourceConfig config) {
            return config.getDb();
        }

        @Override
        public void setTarget(DataSourceConfig config, String target) {
            config.setDb(target);
        }
    },

    USER("user", "用户名") {
        @Override
        public String getTarget(DataSourceConfig config) {
            return config.getUser();
        }

        @Override
        public void setTarget(DataSourceConfig config, String target) {
            config.setUser(target);
        }
    },

    SCHEMA("schema", "数据库模式") {
        @Override
        public String getTarget(DataSourceConfig config) {
            return config.getSchema();
        }

        @Override
        public void setTarget(DataSourceConfig config, String target) {
            config.setSchema(target);
        }
    },

    PROTOCOL("protocol", "协议") {
        @Override
        public String getTarget(DataSourceConfig config) {
            return config.getProtocol();
        }

        @Override
        public void setTarget(DataSourceConfig config, String target) {
            config.setProtocol(target);
        }
    },

    PARAMETER("parameter", "参数") {
        @Override
        public String getTarget(DataSourceConfig config) {
            return config.getParameter();
        }

        @Override
        public void setTarget(DataSourceConfig config, String target) {
            config.setParameter(target);
        }
    },

    DRIVER("driver", "驱动") {
        @Override
        public String getTarget(DataSourceConfig config) {
            return config.getDriver();
        }

        @Override
        public void setTarget(DataSourceConfig config, String target) {
            config.setDriver(target);
        }
    },
    ;


    private final String code;
    private final String desc;

    public abstract String getTarget(DataSourceConfig config);

    public abstract void setTarget(DataSourceConfig config, String target);


    public static ConfigTargetType getByCode(String code) {
        for (ConfigTargetType target : values()) {
            if (target.getCode().equals(code)) {
                return target;
            }
        }
        return null;
    }
}
