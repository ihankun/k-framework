package io.ihankun.framework.mongoplus.conditions.inject.query;

import io.ihankun.framework.mongoplus.conditions.query.QueryChainWrapper;
import io.ihankun.framework.mongoplus.toolkit.ChainWrappers;

import java.util.Map;

/**
 * @author hankun
 **/
public class InjectQueryWrapper extends QueryChainWrapper<Map<String,Object>, InjectQueryWrapper> {

    /**
     * 链式调用
     * @author hankun
     * @date 2023/8/12 2:14
     */
    public QueryChainWrapper<Map<String,Object>, InjectQueryWrapper> lambdaQuery(){
        return ChainWrappers.lambdaQueryChainInject();
    }
}
