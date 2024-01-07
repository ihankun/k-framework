package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.conditions.BuildCondition;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

/**
 * sample策略实现类
 *
 * @author hankun
 **/
public class SampleConcretePipeline implements PipelineStrategy {

    private final Long size;

    public SampleConcretePipeline(Long size) {
        this.size = size;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return BuildCondition.buildSample(size);
    }
}
