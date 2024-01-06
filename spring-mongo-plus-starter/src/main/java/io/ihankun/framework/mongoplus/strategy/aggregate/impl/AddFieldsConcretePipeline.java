package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.conditions.BuildCondition;
import io.ihankun.framework.mongoplus.conditions.interfaces.aggregate.pipeline.AddFields;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * addField策略实现类
 *
 * @author hankun
 **/
public class AddFieldsConcretePipeline implements PipelineStrategy {

    private final List<AddFields> addFieldsList;

    public AddFieldsConcretePipeline(AddFields... addFields) {
        this.addFieldsList = new ArrayList<>(Arrays.asList(addFields));
    }

    public AddFieldsConcretePipeline(List<AddFields> addFieldsList) {
        this.addFieldsList = addFieldsList;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return BuildCondition.buildAddFields(addFieldsList);
    }
}
