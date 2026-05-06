package io.hankun.framework.commons.context;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Objects;

/**
 * @description:
 * @className: KContextLifter
 * @createAt: 2025/8/20 11:24
 * @author: hankun
 */
public class KContextLifter<T> implements CoreSubscriber<T> {

    private final CoreSubscriber<T> subscriber;

    public KContextLifter(CoreSubscriber<T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription s) {
        withContext(subscriber.currentContext(), () -> subscriber.onSubscribe(s));
    }

    @Override
    public void onNext(T t) {
        withContext(subscriber.currentContext(), () -> subscriber.onNext(t));
    }

    @Override
    public void onError(Throwable throwable) {
        withContext(subscriber.currentContext(), () -> subscriber.onError(throwable));
    }

    @Override
    public void onComplete() {
        withContext(subscriber.currentContext(), subscriber::onComplete);
    }

    @NotNull
    @Override
    public Context currentContext() {
        return subscriber.currentContext();
    }

    private void withContext(ContextView context, Runnable r) {
        KContext old = KContextHolder.get();
        KContext kContext = context.getOrDefault(KContext.CONTEXT_KEY, KContextHolder.get());
        if (Objects.equals(old, kContext)) {
            //上下文相同，不需要切换
            r.run();
            return;
        }
        //切换上下文并执行
        try {
            KContextHolder.set(kContext);
            r.run();
        } finally {
            KContextHolder.set(old);
        }
    }
}
