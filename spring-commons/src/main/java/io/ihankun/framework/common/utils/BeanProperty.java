package io.ihankun.framework.common.utils;

import lombok.Data;

/**
 * Bean属性
 *
 * @author hankun
 */

@Data
public class BeanProperty {
    public BeanProperty(){

    }

    public BeanProperty(String name, Class<?> type) {
    }

    private String name;

    private Class<?> type;

    public String name() {
        return this.name;
    }

    public Class<?> type() {
        return this.type;
    }
}