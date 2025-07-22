package io.ihankun.framework.db.build.druid;

import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import io.ihankun.framework.db.build.config.DsConfig;
import io.ihankun.framework.db.build.druid.config.DsDruidConfig;
import io.ihankun.framework.db.build.ds.DataSourceConfig;
import io.ihankun.framework.db.build.entity.ConfigOverrideGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    public DruidConfig createDruidConfig(DataSourceConfig dataSourceConfig) {
        DruidConfig druidConfig = new DruidConfig();
        for (ConfigOverrideGroup group : ConfigOverrideGroup.values()) {
            List<KDruidConfig> KDruidConfigs = group.getDruidConfigList(dsDruidConfig);
            if (!CollectionUtils.isEmpty(KDruidConfigs)) {
                for (KDruidConfig kunKDruidConfig : KDruidConfigs) {
                    combine(druidConfig, getConfig(kunKDruidConfig, group, dataSourceConfig));
                }
            }
        }
        combine(druidConfig, loadOld(dataSourceConfig));
        return druidConfig;
    }

    private DruidConfig getConfig(KDruidConfig KDruidConfig, ConfigOverrideGroup group,
                                                                                DataSourceConfig dataSourceConfig) {
        if (group.matchGroup(dataSourceConfig, KDruidConfig.getGroup())) {
            return KDruidConfig;
        }
        return null;
    }

    private DruidConfig loadOld(DataSourceConfig dataSourceConfig) {
        Map<String, DruidConfig> druid = dsConfig.getDruid().get(dataSourceConfig.getServiceName());
        if (CollectionUtils.isEmpty(druid)) {
            return null;
        }
        return druid.get(dataSourceConfig.getUser());
    }

    public static void combine(DruidConfig config, DruidConfig overrideConfig) {
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
//        if (!CollectionUtils.isEmpty(overrideConfig.getProxyFilters())) {
//            config.getProxyFilters().addAll(overrideConfig.getProxyFilters());
//        }
        if(StringUtils.isNotBlank(overrideConfig.getProxyFilters())) {
            config.setProxyFilters(overrideConfig.getProxyFilters());
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
