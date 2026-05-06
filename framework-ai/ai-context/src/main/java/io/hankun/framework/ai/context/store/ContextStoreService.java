package io.hankun.framework.ai.context.store;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ContextStoreService
 * @createAt: 2025/10/17 16:42
 * @author: hankun
 */
@Component
public class ContextStoreService {

    private final Map<ContextStoreType, ContextStore> contextStoreMap;

    public ContextStoreService(List<ContextStore> contextStores) {
        this.contextStoreMap = new HashMap<>(contextStores.size());
        for (ContextStore contextStore : contextStores) {
            this.contextStoreMap.put(contextStore.type(), contextStore);
        }
    }

    public ContextStore getContextStore(ContextStoreType type) {
        return contextStoreMap.get(type);
    }

    public Collection<ContextStore> getAllContextStores() {
        return contextStoreMap.values();
    }
}
