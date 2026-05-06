package io.hankun.framework.ai.context.store.impl;

import io.hankun.framework.ai.common.redis.KRedisHolder;
import io.hankun.framework.ai.context.store.ContextStore;
import io.hankun.framework.ai.context.store.ContextStoreType;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: AcrossAgentContextStore
 * @createAt: 2025/10/17 16:25
 * @author: hankun
 */
@Component
public class AcrossAgentContextStore extends ContextStore {

    public AcrossAgentContextStore(KRedisHolder kRedisHolder) {
        super(kRedisHolder);
    }

    @Override
    public ContextStoreType type() {
        return ContextStoreType.ACROSS_AGENT;
    }
}
