package io.ihankun.framework.common.thread;

import io.ihankun.framework.common.context.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author hankun
 */
@Slf4j
public class ThreadLocalHolder implements Serializable {

    private LoginUserInfo loginUserInfo;
    private String domain;
    private String businessTime;
    private String gray;

    /**
     * 捕获当前线程上下文信息
     */
    public static ThreadLocalHolder capture() {
        ThreadLocalHolder holder = new ThreadLocalHolder();
        holder.loginUserInfo = LoginUserContext.get();
        holder.domain = DomainContext.get();
        holder.businessTime = BusinessTimeContext.get();
        holder.gray = GrayContext.get();
        log.debug("CommThreadLocalHolder.capture,thread.id={},thread.name={},info={}", Thread.currentThread().getId(), Thread.currentThread().getName(), holder);
        return holder;
    }


    /**
     * 清理当前线程上下文内容
     */
    public static void clear() {
        LoginUserContext.clear();
        DomainContext.clear();
        BusinessTimeContext.clear();
        GrayContext.clear();
        log.debug("CommThreadLocalHolder.clear,thread.id={},thread.name={}", Thread.currentThread().getId(), Thread.currentThread().getName());
    }

    /**
     * 注入到新线程上下文中
     */
    public void inject() {
        LoginUserContext.mock(this.loginUserInfo);
        DomainContext.mock(this.domain);
        BusinessTimeContext.mock(this.businessTime);
        GrayContext.mock(this.gray);
        log.debug("CommThreadLocalHolder.inject,thread.id={},thread.name={},info={}", Thread.currentThread().getId(), Thread.currentThread().getName(), this);
    }

    @Override
    public String toString() {
        return "CommThreadLocalHolder{" + "loginUserInfo=" + loginUserInfo + ", domain='" + domain + '\'' + ", businessTime='" + businessTime + '\'' + ", gray='" + gray + '\'' + '}';
    }
}
