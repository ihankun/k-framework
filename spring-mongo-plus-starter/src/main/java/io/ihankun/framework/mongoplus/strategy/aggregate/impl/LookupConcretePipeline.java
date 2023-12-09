package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

/**
 * lookup策略实现类
 *
 * @author hankun
 **/
public class LookupConcretePipeline implements PipelineStrategy {

    private final BasicDBObject basicDBObject;

    public LookupConcretePipeline(BasicDBObject basicDBObject) {
        this.basicDBObject = basicDBObject;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return basicDBObject;
    }
}
