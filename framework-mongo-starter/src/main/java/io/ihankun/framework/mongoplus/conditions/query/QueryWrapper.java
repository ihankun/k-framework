package io.ihankun.framework.mongoplus.conditions.query;

import io.ihankun.framework.mongoplus.toolkit.ChainWrappers;

/**
 * @author hankun
 **/
public class QueryWrapper<T> extends QueryChainWrapper<T, QueryWrapper<T>> {

    /**
     * 链式调用
     * @author hankun
     * @date 2023/8/12 2:14
    */
    public QueryChainWrapper<T, QueryWrapper<T>> lambdaQuery(){
        return ChainWrappers.lambdaQueryChain();
    }
}
