package io.ihankun.framework.mongoplus.interceptor;

import io.ihankun.framework.mongoplus.model.command.CommandFailed;
import io.ihankun.framework.mongoplus.model.command.CommandStarted;
import io.ihankun.framework.mongoplus.model.command.CommandSucceeded;

/**
 * @author hankun
 * @project mongo-plus
 * @description 拦截器
 * @date 2023-11-22 14:12
 **/
public interface Interceptor {

    /**
     * 最高优先级
     * @see Integer#MIN_VALUE
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * 最低优先级
     * @see Integer#MAX_VALUE
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * 处理命令开始信息
     * @param commandStarted 命令执行开始信息对象
     * @author hankun
     * @date 2023/11/22 14:34
    */
    void commandStarted(CommandStarted commandStarted);

    /**
     * 处理命令成功信息
     * @param commandSucceeded 命令成功信息对象
     * @author hankun
     * @date 2023/11/22 14:35
    */
    void commandSucceeded(CommandSucceeded commandSucceeded);

    /**
     * 处理命令失败信息
     * @param commandFailed 处理命令失败信息对象
     * @author hankun
     * @date 2023/11/22 14:35
    */
    void commandFailed(CommandFailed commandFailed);

    /**
     * 指定拦截器排序
     * @return int
     * @author hankun
     * @date 2023/11/22 16:27
    */
    int getOrder();

}
