package io.ihankun.framework.db.build.druid;

import io.ihankun.framework.db.build.config.DsConfig;
import io.ihankun.framework.db.build.druid.config.DsDruidConfig;
import io.ihankun.framework.db.build.ds.DataSourceConfig;
import io.ihankun.framework.db.build.entity.ConfigOverrideGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hankun
 */
@Slf4j
public class DruidConfigBuilder {

    private final DsDruidConfig dsDruidConfig;

    private final DsConfig dsConfig;

    public DruidConfigBuilder(DsDruidConfig dsDruidConfig,
                              DsConfig dsConfig) {
        this.dsDruidConfig = dsDruidConfig;
        this.dsConfig = dsConfig;
    }

    public com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig createDruidConfig(DataSourceConfig dataSourceConfig) {
        com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig druidConfig = new com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig();
        for (ConfigOverrideGroup group : ConfigOverrideGroup.values()) {
            List<DruidConfig> druidConfigs = group.getDruidConfigList(dsDruidConfig);
            if (!CollectionUtils.isEmpty(druidConfigs)) {
                for (DruidConfig kunDruidConfig : druidConfigs) {
                    combine(druidConfig, getConfig(kunDruidConfig, group, dataSourceConfig));
                }
            }
        }
        combine(druidConfig, loadOld(dataSourceConfig));
        return druidConfig;
    }

    private com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig getConfig(DruidConfig druidConfig, ConfigOverrideGroup group,
                                                                                                  DataSourceConfig dataSourceConfig) {
        if (group.matchGroup(dataSourceConfig, druidConfig.getGroup())) {
            return druidConfig;
        }
        return null;
    }

    private com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig loadOld(DataSourceConfig dataSourceConfig) {
        Map<String, com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig> druid = dsConfig.getDruid().get(dataSourceConfig.getServiceName());
        if (CollectionUtils.isEmpty(druid)) {
            return null;
        }
        return druid.get(dataSourceConfig.getUser());
    }

    public static void combine(com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig config, com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig overrideConfig) {
        if (overrideConfig == null) {
            return;
        }
        Set<String> emptyNames = getNullPropertyNames(overrideConfig);
        emptyNames.add("wall");
        emptyNames.add("slf4j");
        emptyNames.add("log4j");
        emptyNames.add("log4j2");
        emptyNames.add("commonsLog");
        emptyNames.add("stat");
        emptyNames.add("proxyFilters");
        if (!CollectionUtils.isEmpty(overrideConfig.getWall())) {
            config.getWall().putAll(overrideConfig.getWall());
        }
        if (!CollectionUtils.isEmpty(overrideConfig.getSlf4j())) {
            config.getSlf4j().putAll(overrideConfig.getSlf4j());
        }
        if (!CollectionUtils.isEmpty(overrideConfig.getLog4j())) {
            config.getLog4j().putAll(overrideConfig.getLog4j());
        }
        if (!CollectionUtils.isEmpty(overrideConfig.getLog4j2())) {
            config.getLog4j2().putAll(overrideConfig.getLog4j2());
        }
        if (!CollectionUtils.isEmpty(overrideConfig.getCommonsLog())) {
            config.getCommonsLog().putAll(overrideConfig.getCommonsLog());
        }
        if (!CollectionUtils.isEmpty(overrideConfig.getStat())) {
            config.getStat().putAll(overrideConfig.getStat());
        }
        if (!CollectionUtils.isEmpty(overrideConfig.getProxyFilters())) {
            config.getProxyFilters().addAll(overrideConfig.getProxyFilters());
        }
        BeanUtils.copyProperties(overrideConfig, config, emptyNames.toArray(new String[0]));
    }

    public static Set<String> getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames;
    }

}
