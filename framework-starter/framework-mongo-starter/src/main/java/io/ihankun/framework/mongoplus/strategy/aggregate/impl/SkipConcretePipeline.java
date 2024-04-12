package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.enums.AggregateTypeEnum;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

/**
 * skip策略实现类
 *
 * @author hankun
 **/
public class SkipConcretePipeline implements PipelineStrategy {

    private final Long skip;

    public SkipConcretePipeline(Long skip) {
        this.skip = skip;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return new BasicDBObject(AggregateTypeEnum.SKIP.getType(),skip);
    }
}
