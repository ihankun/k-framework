package io.hankun.framework.core.invocation;

import io.hankun.framework.core.invocation.entity.ServiceTableRule;

/**
 * @author hankun
 */
public interface InvocationTableChecker {

    /**
     * 校验sql语句是否合法
     *
     * @param url 数据库连接地址
     * @param sql sql
     */
    void validate(String url, String sql);

    /**
     * 更新数据库权限规则
     *
     * @param rule 规则
     */
    void updateRule(ServiceTableRule rule);
}
