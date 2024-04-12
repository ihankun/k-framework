package io.ihankun.framework.common.ribbon;

import java.util.List;

/**
 * @author hankun
 */
public interface ILoadBalance {

    /**
     * 轮询
     */
    ServiceInstanceWarp balance(List<ServiceInstanceWarp> list);
}
