package io.ihankun.framework.mongoplus.conditions.aggregate;

import io.ihankun.framework.mongoplus.toolkit.ChainWrappers;

/**
 * 管道条件构造
 *
 * @author hankun
 **/
public class AggregateWrapper<T> extends AggregateChainWrapper<T, AggregateWrapper<T>> {

    /**
     * 链式调用
     * @author hankun
     * @date 2023/8/12 2:14
     */
    public AggregateChainWrapper<T, AggregateWrapper<T>> lambdaAggregate(){
        return ChainWrappers.lambdaAggregateChain();
    }

}
