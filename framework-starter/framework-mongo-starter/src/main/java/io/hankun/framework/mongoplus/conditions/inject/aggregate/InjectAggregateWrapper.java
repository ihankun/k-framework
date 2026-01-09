package io.hankun.framework.mongoplus.conditions.inject.aggregate;

import io.hankun.framework.mongoplus.conditions.aggregate.AggregateChainWrapper;
import io.hankun.framework.mongoplus.toolkit.ChainWrappers;

import java.util.Map;

/**
 * @author hankun
 **/
public class InjectAggregateWrapper extends AggregateChainWrapper<Map<String,Object>, InjectAggregateWrapper> {

    /**
     * 链式调用
     * @author hankun
     * @date 2023/8/23 21:09
    */
    public AggregateChainWrapper<Map<String,Object>, InjectAggregateWrapper> lambdaQuery(){
        return ChainWrappers.lambdaAggregateChainInject();
    }

}
