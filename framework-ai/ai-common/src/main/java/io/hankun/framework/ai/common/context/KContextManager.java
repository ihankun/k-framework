package io.hankun.framework.ai.common.context;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.commons.context.KContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: MsunContextManager
 * @createAt: 2025/11/26 14:55
 * @author: hankun
 */
@Component
public class KContextManager {

    private final Map<String, KContext> msunContextMap = new ConcurrentHashMap<>();


    public KContext get(String key) {
        return msunContextMap.get(key);
    }

    public void put(String key, KContext kContext) {
        msunContextMap.put(key, kContext);
    }


    public static String buildKey(CurrentId currentId) {
        return buildKey(currentId.sessionId(), currentId.taskId(), currentId.agentId());
    }

    public static String buildKey(String sessionId, String taskId, String agentId) {
        return sessionId + ":" + taskId + ":" + agentId;
    }

    public void remove(String key) {
        msunContextMap.remove(key);
    }
}
