package io.hankun.framework.ai.agent.call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * @description:
 * @className: AbstractMultiCallFactory
 * @createAt: 2025/12/30 17:43
 * @author: hankun
 */
public abstract class AbstractMultiCallFactory<T extends AbstractMultiCallExecutor> implements ICallExecutorFactory<T> {

    @Lazy
    @Autowired
    private CallExecutorService callExecutorService;


    public ICallExecutor createCallExecutor(String code) {
        return callExecutorService.createCallExecutor(code);
    }
}
