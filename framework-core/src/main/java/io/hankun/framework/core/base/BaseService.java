package io.hankun.framework.core.base;

import io.hankun.framework.core.context.LoginUserContext;
import io.hankun.framework.core.context.LoginUserInfo;

/**
 * @author hankun
 */
public interface BaseService {
    default Long getOrgId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null && loginUser.getOrgId() != null ? loginUser.getOrgId() : BasePO.DEFAULT_ID;
    }

    /**
     * 获取当前登录用户
     */
    default LoginUserInfo getLoginUser() {
        return LoginUserContext.get();
    }
}
