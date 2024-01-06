package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.conditions.BuildCondition;
import io.ihankun.framework.mongoplus.conditions.interfaces.condition.CompareCondition;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import io.ihankun.framework.mongoplus.toolkit.CollUtil;
import com.mongodb.BasicDBObject;

import java.util.List;

/**
 * match策略实现类
 * @author hankun
 **/
public class MatchConcretePipeline implements PipelineStrategy {

    private final List<CompareCondition> compareList;

    private List<BasicDBObject> basicDBObjectList;

    public MatchConcretePipeline(List<CompareCondition> compareList) {
        this.compareList = compareList;
    }

    public MatchConcretePipeline(List<CompareCondition> compareList, List<BasicDBObject> basicDBObjectList) {
        this.compareList = compareList;
        this.basicDBObjectList = basicDBObjectList;
    }

    @Override
    public BasicDBObject buildAggregate() {
        BasicDBObject basicDBObject = BuildCondition.buildQueryCondition(compareList);
        if (CollUtil.isNotEmpty(basicDBObjectList)){
            basicDBObjectList.forEach(basic -> basicDBObject.putAll(basic.toMap()));
        }
        return basicDBObject;
    }
}
