package io.ihankun.framework.mongoplus.conditions.update;

import io.ihankun.framework.mongoplus.toolkit.ChainWrappers;

/**
 * @author hankun
 **/
public class UpdateWrapper<T> extends UpdateChainWrapper<T,UpdateWrapper<T>> {

    /**
     * 链式调用
     * @author hankun
     * @date 2023/8/12 2:14
     */
    public UpdateChainWrapper<T, UpdateWrapper<T>> lambdaUpdate(){
        return ChainWrappers.lambdaUpdateChain();
    }

}
