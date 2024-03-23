package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.conditions.BuildCondition;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

/**
 * out策略实现类
 *
 * @author hankun
 **/
public class OutConcretePipeline implements PipelineStrategy {

    private final String db;

    private final String coll;

    public OutConcretePipeline(String db, String coll) {
        this.db = db;
        this.coll = coll;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return BuildCondition.buildOut(db,coll);
    }
}
