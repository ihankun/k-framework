package io.hankun.framework.commons.context;

import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

/**
 * @description:
 * @className: KContextHook
 * @createAt: 2025/8/20 11:30
 * @author: hankun
 */
public class KContextHook {

    public static void init() {
        Hooks.onEachOperator(Operators.lift((scannable, subscriber) ->
                new KContextLifter<>(subscriber)
        ));
    }
}
