package io.hankun.framework.ai.mcp.app;

import io.hankun.framework.ai.mcp.app.config.KAiMcpConfig;
import io.hankun.framework.ai.mcp.app.entity.KFunction;
import io.hankun.framework.ai.mcp.app.entity.KFunctionList;
import io.hankun.framework.core.context.sys.GrayContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
public abstract class KFunctionManager {

    public static String GRAY = "gray";

    protected final KAiMcpConfig kAiMcpConfig;

    protected KFunctionManager(KAiMcpConfig kAiMcpConfig) {
        this.kAiMcpConfig = kAiMcpConfig;
    }

    public abstract Map<String, KFunctionList> getFunctionsByServiceName(String serviceName);


    public List<KFunction> listByGray() {
        String gray = GrayContext.get();
        if (gray == null) {
            gray = kAiMcpConfig.getDefGray();
        }
        return list(gray, null);
    }

    public Map<String, List<KFunction>> listAll() {
        Map<String, List<KFunction>> resultMap = new HashMap<>();
        for (String service : kAiMcpConfig.getMsunServices()) {
            Map<String, KFunctionList> functionListMap = getFunctionsByServiceName(service);
            if (functionListMap == null) {
                continue;
            }
            for (Map.Entry<String, KFunctionList> entry : functionListMap.entrySet()) {
                List<KFunction> functions = entry.getValue().getFunctions();
                resultMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                        .addAll(functions);
            }
        }
        for (Map.Entry<String, List<KFunction>> entry : resultMap.entrySet()) {
            entry.getValue().sort(Comparator.comparing(KFunction::getSchema)
                    .thenComparing(KFunction::getName)
                    .thenComparing(KFunction::getServiceName));
        }
        return resultMap;
    }

    public List<KFunction> list(String gray, Predicate<KFunction> filter) {
        List<KFunction> collect = new ArrayList<>();
        if ("true".equals(gray)) {
            gray = GRAY;
        }
        if ("false".equals(gray)) {
            gray = "";
        }
        for (String service : kAiMcpConfig.getMsunServices()) {
            Map<String, KFunctionList> functionListMap = getFunctionsByServiceName(service);
            if (functionListMap == null) {
                continue;
            }
            KFunctionList functionList = functionListMap.get(gray);
            if (functionList == null) {
                functionList = functionListMap.get(GRAY);
            }
            if (functionList == null) {
                functionList = functionListMap.get("");
            }
            if (functionList == null) {
                continue;
            }
            for (KFunction function : functionList.getFunctions()) {
                if (filter == null || filter.test(function)) {
                    collect.add(function);
                }
            }
        }
        collect.sort(Comparator.comparing(KFunction::getSchema)
                .thenComparing(KFunction::getName)
                .thenComparing(KFunction::getServiceName));
        log.debug("execute RedisMsunFunctionManager.list: {}", collect);
        return collect;
    }

    public List<KFunctionList> listMsunFunctions(String grayMark, String serviceName) {
        List<KFunctionList> result = new ArrayList<>();
        for (String service : kAiMcpConfig.getMsunServices()) {
            log.info("execute RedisMsunFunctionManager.listMsunFunctions service: {}, grayMark: {}", service, grayMark);
            if (serviceName != null) {
                if (!serviceName.equals(service)) {
                    continue;
                }
            }
            Map<String, KFunctionList> functionListMap = getFunctionsByServiceName(service);
            if (CollectionUtils.isEmpty(functionListMap)) {
                continue;
            }
            if (CollectionUtils.isEmpty(functionListMap)) {
                continue;
            }
            if (ObjectUtils.isEmpty(grayMark)) {
                result.addAll(functionListMap.values());
            } else {
                for (Map.Entry<String, KFunctionList> entry : functionListMap.entrySet()) {
                    if (grayMark.equals(entry.getKey())) {
                        result.add(entry.getValue());
                    }
                }
            }
        }
        log.info("execute RedisMsunFunctionManager.listMsunFunctions: {}", result.size());
        return result;
    }

    public void remove(String serviceName, String grayMark) {
        for (String service : kAiMcpConfig.getMsunServices()) {
            if (serviceName != null) {
                if (!serviceName.equals(service)) {
                    continue;
                }
            }
            if (ObjectUtils.isEmpty(grayMark)) {
                getFunctionsByServiceName(serviceName).clear();
            } else {
                getFunctionsByServiceName(serviceName).remove(service);
            }
        }
    }
}
