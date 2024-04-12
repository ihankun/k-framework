package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.enums.AggregateTypeEnum;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

/**
 * limit策略实现类
 *
 * @author hankun
 **/
public class LimitConcretePipeline implements PipelineStrategy {

    private final Long limit;

    public LimitConcretePipeline(Long limit) {
        this.limit = limit;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return new BasicDBObject(AggregateTypeEnum.LIMIT.getType(),limit);
    }
}
