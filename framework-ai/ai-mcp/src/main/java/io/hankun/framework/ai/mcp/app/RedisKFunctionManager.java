package io.hankun.framework.ai.mcp.app;

import io.hankun.framework.ai.mcp.app.config.KAiMcpConfig;
import io.hankun.framework.ai.mcp.app.entity.KFunction;
import io.hankun.framework.ai.mcp.app.entity.KFunctionList;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: RedisKFunctionManager
 * @createAt: 2025/5/29 09:19
 * @author: hankun
 */
@Slf4j
@ConditionalOnProperty(
        prefix = "k.ai.mcp",
        name = {"registerType"},
        havingValue = "redis",
        matchIfMissing = false
)
@Component
public class RedisKFunctionManager extends KFunctionManager {

    private final RedissonClient redissonClient;

    private final Map<String, RMap<String, KFunctionList>> functionMap = new ConcurrentHashMap<>();

    public RedisKFunctionManager(RedissonClient redissonClient,
                                 KAiMcpConfig kAiMcpConfig) {
        super(kAiMcpConfig);
        this.redissonClient = redissonClient;
    }

    public RMap<String, KFunctionList> getFunctionListMap(String serviceName) {
        return functionMap.computeIfAbsent(serviceName, key -> redissonClient.getLocalCachedMap("ai-functions:" +
                        kAiMcpConfig.getRedisPrefix() + key,
                LocalCachedMapOptions.<String, KFunctionList>defaults()
                        .cacheSize(500)));
    }

    public void register(KFunctionList functionList) {
        if (functionList == null) {
            throw new IllegalArgumentException("functionList can not be null");
        }
        if (functionList.getFunctions() == null) {
            throw new IllegalArgumentException("functionList.functions can not be null");
        }
        if (functionList.getServiceName() == null) {
            throw new IllegalArgumentException("functionList.serviceName can not be null");
        }
        if (functionList.getGrayMark() == null) {
            functionList.setGrayMark("");
        }
        for (KFunction function : functionList.getFunctions()) {
            function.setServiceName(functionList.getServiceName());
            function.setGrayMark(functionList.getGrayMark());
        }
        getFunctionListMap(functionList.getServiceName()).put(functionList.getGrayMark(), functionList);
    }

    @Override
    public Map<String, KFunctionList> getFunctionsByServiceName(String serviceName) {
        return getFunctionListMap(serviceName);
    }

}
