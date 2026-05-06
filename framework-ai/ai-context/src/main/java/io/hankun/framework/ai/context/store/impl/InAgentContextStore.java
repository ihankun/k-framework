package io.hankun.framework.ai.context.store.impl;

import io.hankun.framework.ai.common.redis.KRedisHolder;
import io.hankun.framework.ai.context.store.ContextStore;
import io.hankun.framework.ai.context.store.ContextStoreType;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: InAgentContextStore
 * @createAt: 2025/10/17 16:26
 * @author: hankun
 */
@Component
public class InAgentContextStore extends ContextStore {

    public InAgentContextStore(KRedisHolder kRedisHolder) {
        super(kRedisHolder);
    }

    @Override
    public ContextStoreType type() {
        return ContextStoreType.IN_AGENT;
    }
}
