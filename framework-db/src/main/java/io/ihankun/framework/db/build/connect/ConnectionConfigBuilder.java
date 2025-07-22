package io.ihankun.framework.db.build.connect;

import io.ihankun.framework.db.build.BuildInfoHolder;
import io.ihankun.framework.db.build.config.DsConfigReader;
import io.ihankun.framework.db.build.connect.config.DsOverrideConfig;
import io.ihankun.framework.db.build.connect.entity.ConnectionConfig;
import io.ihankun.framework.db.build.connect.impl.StandardConfigOverrider;
import io.ihankun.framework.db.build.ds.DataSourceConfig;
import io.ihankun.framework.db.build.entity.ConfigOverrideGroup;
import io.ihankun.framework.db.build.entity.PassGenerateType;
import io.ihankun.framework.db.build.util.ConnectResult;
import io.ihankun.framework.db.build.util.ConnectTester;
import io.ihankun.framework.db.build.util.DbUrlHelper;
import io.ihankun.framework.db.build.util.PasswordHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Slf4j
public class ConnectionConfigBuilder {

    private final List<ConfigOverriderFilter> filters = new ArrayList<>();

    private final DsConfigReader dsConfigReader;

    private final ConnectTester connectTester;

    public ConnectionConfigBuilder(List<ConfigOverriderFilter> configOverriderFilters,
                                   DsConfigReader dsConfigReader,
                                   DsOverrideConfig dsOverrideConfig,
                                   ConnectTester connectTester) {
        this.dsConfigReader = dsConfigReader;
        this.connectTester = connectTester;
        Map<String, ConfigOverriderFilter> map = new HashMap<>();
        for (ConfigOverriderFilter filter : configOverriderFilters) {
            map.put(filter.name(), filter);
        }
        for (ConfigOverrideGroup group : ConfigOverrideGroup.values()) {
            StandardConfigOverrider standardConfigOverrider = new StandardConfigOverrider(dsOverrideConfig, group);
            map.put(standardConfigOverrider.name(), standardConfigOverrider);
        }
        this.filters.addAll(map.values().stream().sorted(Comparator.comparingInt(ConfigOverriderFilter::order)).collect(Collectors.toList()));
    }

    public ConnectionConfig build(DataSourceConfig dataSourceConfig, BuildInfoHolder buildInfoHolder) {
        for (ConfigOverriderFilter filter : filters) {
            filter.before(dataSourceConfig, buildInfoHolder);
        }
        ConnectionConfig config = new ConnectionConfig();
        config.setDriverClassName(dataSourceConfig.getDriver());
        String address = getAddress(dataSourceConfig);
        if (ObjectUtils.isEmpty(address)) {
            buildInfoHolder.addMessage("域名[%s]对应的数据库[%s]连接地址为空，请检查配置",
                    dataSourceConfig.getDomain(), dataSourceConfig.getDbMark());
            return null;
        }
        config.setUrl(DbUrlHelper.buildUrl(dataSourceConfig, address));
        config.setUsername(dataSourceConfig.getUser());
        config.setPassword("");
        try (Connection connection = connectionToDb(dataSourceConfig, config, buildInfoHolder)) {
            if (connection != null) {
                for (ConfigOverriderFilter filter : filters) {
                    filter.after(dataSourceConfig, config, buildInfoHolder, connection);
                }
                return config;
            } else {
                buildInfoHolder.addMessage("连接数据库失败，url=%s,userName=%s", config.getUrl(), config.getUsername());
                return null;
            }
        } catch (Throwable e) {
            buildInfoHolder.addMessage("连接数据库失败，url=%s,userName=%s", config.getUrl(), config.getUsername());
            return null;
        }
    }


    public String getAddress(DataSourceConfig dataSourceConfig) {
        return dsConfigReader.getAddress(dataSourceConfig);
    }

    private String getPass(DataSourceConfig dataSourceConfig) {
        return dsConfigReader.getPass(dataSourceConfig);
    }

    private String getSpecialPass(DataSourceConfig dataSourceConfig) {
        return dsConfigReader.getSpecialPass(dataSourceConfig);
    }

    private String decryptPass(DataSourceConfig dataSourceConfig, String pass) {
        String envKey = dsConfigReader.getEnvKey(dataSourceConfig);
        return PasswordHelper.decryptPass(envKey, pass);
    }

    public String buildPass(DataSourceConfig dataSourceConfig) {
        String envKey = dsConfigReader.getEnvKey(dataSourceConfig);
        String domainKey = dsConfigReader.getDomainKey(dataSourceConfig);
        if (StringUtils.isEmpty(domainKey)) {
            return "";
        }
        String base = dsConfigReader.getPassGenerateInfo(dataSourceConfig);
        if (StringUtils.isEmpty(base)) {
            base = PasswordHelper.BASE;
        }
        PassGenerateType type = dsConfigReader.getPassGenerateType(dataSourceConfig);
        log.info("密码生成方式={},环境key={},域名key={},用户={},字符集合={}", type.getDesc(), envKey, domainKey,
                dataSourceConfig.getUser(), base);
        return type.buildPass(envKey, domainKey,
                dataSourceConfig.getUser(), base);
    }

    private Connection connectionToDb(DataSourceConfig dataSourceConfig, ConnectionConfig connectionConfig,
                                      BuildInfoHolder buildInfoHolder) {
        String configPass = getPass(dataSourceConfig);
        String specialPass = getSpecialPass(dataSourceConfig);
        String buildPass = buildPass(dataSourceConfig);
        List<PasswordHolder> passList = new ArrayList<>();
        if (!StringUtils.isEmpty(specialPass)) {
            String decryptSpecialPass = decryptPass(dataSourceConfig, specialPass);
            passList.add(new PasswordHolder("单数据库密码", specialPass));
            if (!Objects.equals(decryptSpecialPass, specialPass)) {
                passList.add(new PasswordHolder("解密后的单数据库密码", decryptSpecialPass));
            }
        }
        if (!StringUtils.isEmpty(configPass)) {
            passList.add(new PasswordHolder("配置的密码", configPass));
            String decryptPass = decryptPass(dataSourceConfig, configPass);
            if (!Objects.equals(decryptPass, configPass)) {
                passList.add(new PasswordHolder("解密后的密码", decryptPass));
            }
        }
        if (!StringUtils.isEmpty(buildPass)) {
            passList.add(new PasswordHolder("构建的密码", buildPass));
        }
        List<String> failedList = new ArrayList<>(passList.size());
        int passFailedCount = 0;
        for (PasswordHolder pass : passList) {
            ConnectResult result = connectTester.tryConnect(connectionConfig, pass.getPass());
            if (result.getConnection() != null) {
                connectionConfig.setPassword(pass.getPass());
                return result.getConnection();
            } else {
                log.error("使用【{}】连接失败:ds={},domain={}，url={},e=", pass.getDesc(), dataSourceConfig.getDs(),
                        dataSourceConfig.getDomain(), connectionConfig.getUrl(), result.getThrowable());
                String message = pass.buildMessage(result);
                failedList.add(message);
                if (isPasswordError(result)) {
                    passFailedCount++;
                }
            }
        }
        if (passFailedCount == failedList.size()) {
            failedList.add(0, "所有密码都错误，请检查配置或者数据库密码");
        }
        buildInfoHolder.addMessage(String.join(",", failedList));
        return null;
    }

    @Getter
    @AllArgsConstructor
    private static class PasswordHolder {

        private final String desc;

        private final String pass;

        public String buildMessage(ConnectResult result) {
            return String.format("使用【%s】连接失败: code=【%s】,message=【%s】", getDesc(), result.getCode(), result.getMessage());
        }
    }


    private static boolean isPasswordError(ConnectResult result) {
        return StringUtils.hasText(result.getMessage()) && result.getMessage().contains("password authentication failed");
    }


}
