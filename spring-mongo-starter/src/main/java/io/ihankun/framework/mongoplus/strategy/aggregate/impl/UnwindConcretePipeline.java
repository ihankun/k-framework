package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.conditions.BuildCondition;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

/**
 * unwind策略实现类
 *
 * @author hankun
 **/
public class UnwindConcretePipeline implements PipelineStrategy {

    private final String field;

    private final Boolean preserveNullAndEmptyArrays;

    public UnwindConcretePipeline(String field, Boolean preserveNullAndEmptyArrays) {
        this.field = field;
        this.preserveNullAndEmptyArrays = preserveNullAndEmptyArrays;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return BuildCondition.buildUnwind(preserveNullAndEmptyArrays,field);
    }
}
