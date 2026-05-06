package io.hankun.framework.ai.context.entity;


import io.hankun.framework.core.context.sys.DomainContext;
import io.hankun.framework.core.context.user.LoginUserContext;

import java.util.Map;

/**
 * @description: 调用者信息
 * @className: CallerInfo
 * @createAt: 2025/11/5 10:06
 * @author: hankun
 */
public record CallerInfo(String uniqueId, Long threadId, Map<String, Object> expandInfo) {


    public static CallerInfo of(Long threadId, Map<String, Object> expandInfo) {
        return new CallerInfo(buildUserKey(), threadId, expandInfo);
    }

    public static String buildUserKey() {
        return DomainContext.get() + ":" + LoginUserContext.get().getUserId();
    }
}
