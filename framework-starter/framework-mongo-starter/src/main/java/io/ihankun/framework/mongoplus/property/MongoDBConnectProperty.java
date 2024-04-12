package io.ihankun.framework.mongoplus.property;

import io.ihankun.framework.mongoplus.model.BaseProperty;
import io.ihankun.framework.mongoplus.model.SlaveDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author hankun
 * 属性文件配置
 * @since 2023-02-09 14:29
 **/
@ConfigurationProperties(prefix = "mongo-plus.data.mongodb")
public class MongoDBConnectProperty extends BaseProperty {

    /**
     * 从数据源
     * @author: hankun
     * @date: 2023/2/18 15:03
     **/
    private List<SlaveDataSource> slaveDataSource;

    public List<SlaveDataSource> getSlaveDataSource() {
        return this.slaveDataSource;
    }

    public void setSlaveDataSource(final List<SlaveDataSource> slaveDataSource) {
        this.slaveDataSource = slaveDataSource;
    }

    public MongoDBConnectProperty(final List<SlaveDataSource> slaveDataSource) {
        this.slaveDataSource = slaveDataSource;
    }

    public MongoDBConnectProperty() {
    }

}
