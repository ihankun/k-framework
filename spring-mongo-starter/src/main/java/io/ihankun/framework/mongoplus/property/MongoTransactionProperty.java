package io.ihankun.framework.mongoplus.property;

import io.ihankun.framework.mongoplus.cache.global.PropertyCache;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hankun
 * @project mongo-plus
 * @description
 * @date 2023-10-10 13:07
 **/
@ConfigurationProperties(prefix = "mongo-plus.spring")
public class MongoTransactionProperty {

    private Boolean transaction = false;

    public Boolean getTransaction() {
        return transaction;
    }

    public void setTransaction(Boolean transaction) {
        PropertyCache.transaction = transaction;
        this.transaction = transaction;
    }

}
