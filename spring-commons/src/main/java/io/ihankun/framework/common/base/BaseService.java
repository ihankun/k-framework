package io.ihankun.framework.common.base;

import io.ihankun.framework.common.context.LoginUserContext;
import io.ihankun.framework.common.context.LoginUserInfo;

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
