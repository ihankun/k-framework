package io.ihankun.framework.mongoplus.conditions.inject.update;

import io.ihankun.framework.mongoplus.conditions.update.UpdateChainWrapper;
import io.ihankun.framework.mongoplus.toolkit.ChainWrappers;

import java.util.Map;

/**
 * @author hankun
 **/
public class InjectUpdateWrapper extends UpdateChainWrapper<Map<String,Object>,InjectUpdateWrapper> {

    /**
     * 链式调用
     * @author hankun
     * @date 2023/8/12 2:14
     */
    public UpdateChainWrapper<Map<String,Object>, InjectUpdateWrapper> lambdaUpdate(){
        return ChainWrappers.lambdaUpdateChainInject();
    }

}
