package io.hankun.framework.commons.context;

import io.hankun.framework.core.context.user.LoginUserInfo;
import org.apache.logging.log4j.ThreadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.util.context.Context;

/**
 * @description:
 * @className: KContextHolder
 * @createAt: 2025/8/19 14:50
 * @author: hankun
 */
public class KContextHolder {

    private static final ThreadLocal<KContext> context = new ThreadLocal<>();

    public static final String GRAY = "gray";

    public static final String DOMAIN = "domain";

    public static final String HOSPITAL_ID = "hospitalId";

    public static final String USER_ID = "userId";

    public static final String TRACE_ID = "traceId";

    public static String getTraceId() {
        KContext kContext = get();
        if (kContext == null) {
            return null;
        }
        return kContext.getTraceId();
    }

    public static void setTraceId(String traceId) {
        KContext kContext = getOrDef();
        kContext.setTraceId(traceId);
        ThreadContext.put(TRACE_ID, traceId);
    }

    public static void clearTraceId() {
        KContext kContext = get();
        if (kContext == null) {
            return;
        }
        kContext.setTraceId(null);
        ThreadContext.remove(TRACE_ID);
    }

    public static String getDomain() {
        KContext kContext = get();
        if (kContext == null) {
            return null;
        }
        return kContext.getDomain();
    }

    public static void setDomain(String domain) {
        KContext kContext = getOrDef();
        kContext.setDomain(domain);
        ThreadContext.put(DOMAIN, domain);
    }

    public static void clearDomain() {
        KContext kContext = get();
        if (kContext == null) {
            return;
        }
        kContext.setDomain(null);
        ThreadContext.remove(DOMAIN);
    }


    public static LoginUserInfo getLoginUserInfo() {
        KContext kContext = get();
        if (kContext == null) {
            return null;
        }
        return kContext.getLoginUserInfo();
    }

    public static void setLoginUserInfo(LoginUserInfo loginUserInfo) {
        KContext kContext = getOrDef();
        kContext.setLoginUserInfo(loginUserInfo);
        //ThreadContext.put(HOSPITAL_ID, String.valueOf(loginUserInfo.getHospitalId()));
        ThreadContext.put(USER_ID, String.valueOf(loginUserInfo.getUserId()));
    }

    public static void clearLoginUserInfo() {
        KContext kContext = get();
        if (kContext == null) {
            return;
        }
        kContext.setLoginUserInfo(null);
        ThreadContext.remove(HOSPITAL_ID);
        ThreadContext.remove(USER_ID);
    }

    public static String getGray() {
        KContext kContext = get();
        if (kContext == null) {
            return null;
        }
        return kContext.getGray();
    }

    public static void setGray(String gray) {
        KContext kContext = getOrDef();
        kContext.setGray(gray);
        ThreadContext.put(GRAY, gray);
    }

    public static void clearGray() {
        KContext kContext = get();
        if (kContext == null) {
            return;
        }
        kContext.setGray(null);
        ThreadContext.remove(GRAY);
    }


    public static KContext capture() {
        KContext kContext = get();
        if (kContext == null) {
            return null;
        }
        return kContext.copy();
    }

    @Nullable
    public static KContext get() {
        return context.get();
    }

    public static Context captureReactorContext() {
        KContext kContext = context.get();
        if (kContext == null) {
            return Context.empty();
        }
        return KContext.toReactorContext(kContext);
    }

    public static void set(KContext kContext) {
        if (kContext == null) {
            clear();
            return;
        }
        context.set(kContext);
        ThreadContext.put(TRACE_ID, kContext.getTraceId());
        ThreadContext.put(DOMAIN, kContext.getDomain());
        LoginUserInfo loginUserInfo = kContext.getLoginUserInfo();
        if (loginUserInfo != null) {
            //ThreadContext.put(HOSPITAL_ID, String.valueOf(loginUserInfo.getHospitalId()));
            ThreadContext.put(USER_ID, String.valueOf(loginUserInfo.getUserId()));
        }
        ThreadContext.put(GRAY, kContext.getGray());
    }


    @NotNull
    private static KContext getOrDef() {
        KContext kContext = context.get();
        if (kContext == null) {
            kContext = new KContext();
            context.set(kContext);
        }
        return kContext;
    }

    public static <T> T getData(String key) {
        KContext kContext = get();
        if (kContext == null) {
            return null;
        }
        return kContext.getData(key);
    }

    public static <T> void setData(String key, T value) {
        if (value == null) {
            removeData(key);
            return;
        }
        KContext kContext = getOrDef();
        kContext.setData(key, value);
    }

    public static void removeData(String key) {
        KContext kContext = getOrDef();
        kContext.removeData(key);
    }

    public static void clear() {
        context.remove();
        ThreadContext.clearAll();
    }
}
