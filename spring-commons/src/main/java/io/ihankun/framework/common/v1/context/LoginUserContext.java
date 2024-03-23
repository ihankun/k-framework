package io.ihankun.framework.common.v1.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;

/**
 * @author hankun
 */
@Slf4j
public class LoginUserContext {

    public static final String LOGIN_USER_KEY = "login-user";

    private static final ThreadLocal<LoginUserInfo> CONTEXT_HOLDER = new NamedThreadLocal<>(LOGIN_USER_KEY);

    public static LoginUserInfo get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public static void mock(LoginUserInfo loginUserInfo) {
        if(loginUserInfo!=null) {
            CONTEXT_HOLDER.set(loginUserInfo);
        }
    }

}
