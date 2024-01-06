package io.ihankun.framework.mongoplus.strategy.aggregate.impl;

import io.ihankun.framework.mongoplus.conditions.BuildCondition;
import io.ihankun.framework.mongoplus.conditions.interfaces.aggregate.pipeline.ReplaceRoot;
import io.ihankun.framework.mongoplus.strategy.aggregate.PipelineStrategy;
import io.ihankun.framework.mongoplus.support.SFunction;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * replaceRoot策略实现类
 *
 * @author hankun
 **/
public class RootConcretePipelineReplace implements PipelineStrategy {

    private final Boolean reserveOriginalDocument;

    private final List<ReplaceRoot> replaceRootList;

    public RootConcretePipelineReplace(Boolean reserveOriginalDocument, ReplaceRoot... replaceRoot) {
        this.reserveOriginalDocument = reserveOriginalDocument;
        this.replaceRootList = new ArrayList<>(Arrays.asList(replaceRoot));
    }

    public RootConcretePipelineReplace(Boolean reserveOriginalDocument, String... field) {
        this.reserveOriginalDocument = reserveOriginalDocument;
        this.replaceRootList = new ArrayList<ReplaceRoot>(){{
            for (String col : field) {
                add(new ReplaceRoot(col,col));
            }
        }};
    }

    @SafeVarargs
    public <T> RootConcretePipelineReplace(Boolean reserveOriginalDocument, SFunction<T,Object>... field) {
        this.reserveOriginalDocument = reserveOriginalDocument;
        this.replaceRootList = new ArrayList<ReplaceRoot>(){{
            for (SFunction<T,Object> col : field) {
                add(new ReplaceRoot(col.getFieldNameLine(),col.getFieldNameLine()));
            }
        }};
    }

    public RootConcretePipelineReplace(Boolean reserveOriginalDocument, List<ReplaceRoot> replaceRootList) {
        this.reserveOriginalDocument = reserveOriginalDocument;
        this.replaceRootList = replaceRootList;
    }

    @Override
    public BasicDBObject buildAggregate() {
        return BuildCondition.buildReplaceRoot(reserveOriginalDocument,replaceRootList);
    }
}
