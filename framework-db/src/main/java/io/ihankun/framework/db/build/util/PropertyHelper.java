package io.ihankun.framework.db.build.util;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import io.ihankun.framework.db.build.connect.entity.ConnectionConfig;
import org.springframework.beans.BeanUtils;

/**
 * @author hankun
 */
public class PropertyHelper {
    public static DataSourceProperty buildProperty(ConnectionConfig config,
                                                   String poolName,
                                                   DruidConfig dataSourceConfig,
                                                   boolean seata) {
        DataSourceProperty property = new DataSourceProperty();
        property.setPoolName(poolName);
        property.setDriverClassName(config.getDriverClassName());
        property.setUrl(config.getUrl());
        property.setUsername(config.getUsername());
        property.setPassword(config.getPassword());
        property.setLazy(true);
        property.setDruid(dataSourceConfig);
        property.setSeata(seata);
        return property;
    }

    public static DataSourceProperty buildOutput(DataSourceProperty property) {
        DataSourceProperty output = new DataSourceProperty();
        BeanUtils.copyProperties(property, output);
        output.setPassword("******");
        return output;
    }
}
