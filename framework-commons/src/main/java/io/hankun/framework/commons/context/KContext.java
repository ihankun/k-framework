package io.hankun.framework.commons.context;

import io.hankun.framework.commons.utils.ContextUtil;
import io.hankun.framework.core.context.user.LoginUserInfo;
import lombok.Getter;
import lombok.Setter;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KContext
 * @createAt: 2025/8/20 11:07
 * @author: hankun
 */
public class KContext {

    public static final String CONTEXT_KEY = "k-context";

    @Getter
    @Setter
    private String traceId;

    @Getter
    @Setter
    private String gray;

    @Getter
    @Setter
    private String domain;

    @Getter
    @Setter
    private LoginUserInfo loginUserInfo;

    private final Map<String, Object> context = new HashMap<>();


    public Context toReactorContext() {
        return toReactorContext(this);
    }

    public <T> T getData(String key) {
        return ContextUtil.getData(context, key);
    }

    public <T> List<T> getDataList(String key) {
        return ContextUtil.getDataList(context, key);
    }

    public <T> void setData(String key, T value) {
        if (value == null) {
            removeData(key);
            return;
        }
        ContextUtil.setData(context, key, value);
    }

    public void removeData(String key) {
        context.remove(key);
    }

    public static Context toReactorContext(KContext context) {
        return Context.of(CONTEXT_KEY, context);
    }

    public KContext copy() {
        KContext kContext = new KContext();
        kContext.setTraceId(this.traceId);
        kContext.setGray(this.gray);
        kContext.setDomain(this.domain);
        kContext.setLoginUserInfo(this.loginUserInfo);
        kContext.context.putAll(this.context);
        return kContext;
    }

    public static KContext fromReactorContext(ContextView context) {
        return context.getOrDefault(CONTEXT_KEY, null);
    }

    public static KContext of(String traceId, String gray, String domain, LoginUserInfo loginUserInfo) {
        KContext kContext = new KContext();
        kContext.setTraceId(traceId);
        kContext.setGray(gray);
        kContext.setDomain(domain);
        kContext.setLoginUserInfo(loginUserInfo);
        return kContext;
    }
}
