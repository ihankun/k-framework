package io.hankun.framework.ai.agent.call;

/**
 * @description:
 * @className: ICallExecutorFactory
 * @createAt: 2025/12/23 10:28
 * @author: hankun
 */
public interface ICallExecutorFactory<T extends ICallExecutor> {

    String code();

    T createExecutor();
}
