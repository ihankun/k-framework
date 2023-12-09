package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.conditions.BuildCondition;
import io.ihankun.framework.mongoplus.conditions.interfaces.aggregate.pipeline.Projection;
import io.ihankun.framework.mongoplus.constant.SqlOperationConstant;
import io.ihankun.framework.mongoplus.enums.ProjectionEnum;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * project策略实现类
 *
 * @author hankun
 **/
public class ProjectConcretePipeline implements PipelineStrategy {

    private final List<Projection> projectionList;

    public ProjectConcretePipeline(Projection... projection) {
        this.projectionList = new ArrayList<>(Arrays.asList(projection));
    }

    public ProjectConcretePipeline(boolean displayId, Projection... projection) {
        this.projectionList = new ArrayList<>(Arrays.asList(projection));
        if (!displayId) {
            this.projectionList.add(Projection.builder().column(SqlOperationConstant._ID).value(ProjectionEnum.NONE.getValue()).build());
        }
    }

    public ProjectConcretePipeline(List<Projection> projectionList) {
        this.projectionList = projectionList;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return BuildCondition.buildProjection(projectionList);
    }
}
