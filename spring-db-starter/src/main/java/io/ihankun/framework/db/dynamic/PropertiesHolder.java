package io.ihankun.framework.db.dynamic;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Component
public class PropertiesHolder {
    private static final String DS_SPLIT = "_";

    private Map<String, DataSourceProperty> propertyMap;

    private Map<String, List<String>> targetInfo;

    public List<String> getAlias(String mark) {
        return targetInfo.get(mark);
    }

    public void setPropertyMap(Map<String, DataSourceProperty> map) {
        propertyMap = new HashMap<>(map.size());
        for (Map.Entry<String, DataSourceProperty> entry : map.entrySet()) {
            String alias;
            boolean withSuffix = false;
            if (entry.getKey().contains(DS_SPLIT)) {
                alias = entry.getKey().split(DS_SPLIT)[0];
                withSuffix = true;
            } else {
                alias = entry.getKey();
            }
            if (!(propertyMap.containsKey(alias) && withSuffix)) {
                propertyMap.put(alias, entry.getValue());
            }
        }
        targetInfo = new HashMap<>(8);
        for (Map.Entry<String, DataSourceProperty> entry : propertyMap.entrySet()) {
            DataSourceProperty property = new DataSourceProperty();
            BeanUtils.copyProperties(entry.getValue(), property);
            entry.setValue(property);
        }
    }

    public DataSourceProperty getModel(String key) {
        if (propertyMap.containsKey(key)) {
            DataSourceProperty property = new DataSourceProperty();
            BeanUtils.copyProperties(propertyMap.get(key), property);
            return property;
        }
        return null;
    }
}
