package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

/**
 * 默认实现策略
 *
 * @author hankun
 **/
public class DefaultConcretePipeline implements PipelineStrategy {

    private final BasicDBObject basicDBObject;

    public DefaultConcretePipeline(BasicDBObject basicDBObject) {
        this.basicDBObject = basicDBObject;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return basicDBObject;
    }
}
