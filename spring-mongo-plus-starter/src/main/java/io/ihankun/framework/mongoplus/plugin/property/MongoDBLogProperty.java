package io.ihankun.framework.mongoplus.plugin.property;

import io.ihankun.framework.mongoplus.cache.global.OrderCache;
import io.ihankun.framework.mongoplus.cache.global.PropertyCache;
import io.ihankun.framework.mongoplus.interceptor.business.LogInterceptor;

/**
 * @author hankun
 * 日志属性
 * @since 2023-06-07 23:07
 **/
public class MongoDBLogProperty {

    /**
     * 是否开启日志
     * @author: hankun
     * @date: 2023/6/7 23:08
     **/
    private Boolean log = false;

    /**
     * 是否打开格式化sql
     * @author hankun
     * @date 2023/8/29 0:52
    */
    private Boolean format = false;

    /**
     * 指定日志拦截器的order，默认为0
     * @author hankun
     * @date 2023/11/22 19:01
     */
    private int logOrder = 0;

    public int getLogOrder() {
        return logOrder;
    }

    public void setLogOrder(int logOrder) {
        OrderCache.LOG_ORDER = logOrder;
        this.logOrder = logOrder;
    }


    public Boolean getLog() {
        return this.log;
    }

    public Boolean getFormat() {
        return this.format;
    }

    public void setLog(final Boolean log) {
        this.log = log;
    }

    public void setFormat(final Boolean format) {
        PropertyCache.format = format;
        this.format = format;
    }

    public MongoDBLogProperty(final Boolean log, final Boolean format) {
        this.log = log;
        this.format = format;
    }

    public MongoDBLogProperty() {
    }

}
