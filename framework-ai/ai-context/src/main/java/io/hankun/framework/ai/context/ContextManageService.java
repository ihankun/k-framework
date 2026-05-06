package io.hankun.framework.ai.context;

import io.hankun.framework.ai.common.context.CurrentIdHolder;
import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.session.OperateStatus;
import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.common.session.node.TaskNodeStatus;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.common.util.TopologicalSort;
import io.hankun.framework.ai.context.entity.*;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.context.store.ContextStore;
import io.hankun.framework.ai.context.store.ContextStoreService;
import io.hankun.framework.ai.context.store.ContextStoreType;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @description:
 * @className: ContextManageService
 * @createAt: 2025/10/16 14:56
 * @author: hankun
 */
@Slf4j
@Component
public class ContextManageService {

    @Getter
    private final List<ContextRegister<?>> contextRegisters;

    private final Map<String, ContextRegister<?>> contextKeyRegisterMap;

    private final Map<Class<?>, ContextRegister<?>> contextRegisterMap;

    private final Map<Class<?>, ContextStore> contextStoreMap;

    private final ExecutorService executorService;

    private final ContextStoreService contextStoreService;


    public ContextManageService(List<ContextRegister<? extends IContext>> contextRegisters, ContextStoreService contextStoreService) {
        this.contextRegisters = new ArrayList<>(contextRegisters);
        this.contextStoreService = contextStoreService;
        this.contextRegisters.sort(Comparator.comparingInt(ContextRegister::getOrder));
        this.contextRegisterMap = new HashMap<>(contextRegisters.size());
        this.contextKeyRegisterMap = new HashMap<>(contextRegisters.size());
        this.contextStoreMap = new HashMap<>(contextRegisters.size());
        TopologicalSort.Builder<Class<? extends IContext>> builder = new TopologicalSort.Builder<>();
        for (ContextRegister<? extends IContext> contextRegister : contextRegisters) {
            Class<? extends IContext> type = contextRegister.dataType();
            contextRegisterMap.put(type, contextRegister);
            ContextRegister<? extends IContext> old = contextKeyRegisterMap.get(contextRegister.getKey());
            if (old != null) {
                log.error("【初始化上下文管理器】重复的注册器, key: {}, register: {}, old: {}", contextRegister.getKey(), contextRegister, old);
                throw new RuntimeException("【初始化上下文管理器】重复的注册器, key: " + contextRegister.getKey());
            }
            contextKeyRegisterMap.put(contextRegister.getKey(), contextRegister);
            contextStoreMap.put(type, contextStoreService.getContextStore(contextRegister.lifecycle().getStoreType()));
            ContextDependent dependent = contextRegister.getClass().getAnnotation(ContextDependent.class);
            if (dependent != null) {
                //dependentMap.put(contextRegister.getKey(), dependent);
                for (Class<? extends IContext> dependentKey : dependent.value()) {
                    builder.addEdge(dependentKey, type);
                }
            }
        }
        ThreadFactory factory = Thread.ofVirtual().name("context-init-", 0L).factory();
        this.executorService = Executors.newThreadPerTaskExecutor(factory);
        TopologicalSort<Class<? extends IContext>> sort = builder.build();
        sort.topologicalSortKahn();
    }

    public Map<Class<? extends IContext>, ContextRegister<? extends IContext>> getContextMap(Collection<Class<? extends IContext>> contexts) {
        Map<Class<? extends IContext>, ContextRegister<? extends IContext>> contextMap = new HashMap<>(contexts.size());
        for (Class<? extends IContext> context : contexts) {
            ContextRegister<? extends IContext> contextRegister = getContextRegister(context);
            if (contextRegister == null) {
                continue;
            }
            if (!contextMap.containsKey(context)) {
                contextMap.put(context, contextRegister);
            }
        }
        return contextMap;
    }

    public ContextAccess loadContext(String sessionId, String agentId,
                                     Collection<Class<? extends IContext>> contexts,
                                     Map<String, Object> bindData) {
        Map<Class<? extends IContext>, ContextRegister<? extends IContext>> contextMap = new HashMap<>(contexts.size());
        List<Class<? extends IContext>> contextList = new ArrayList<>(contexts.size());
        for (Class<? extends IContext> context : contexts) {
            ContextRegister<? extends IContext> contextRegister = getContextRegister(context);
            if (contextRegister == null) {
                log.warn("【加载上下文】未找到上下文注册器, context: {}", context);
                continue;
            }
            if (contextMap.containsKey(context)) {
                log.warn("【加载上下文】重复的上下文, context: {}", context);
                continue;
            }
            contextMap.put(context, contextRegister);
            contextList.add(context);
        }
        contextList.sort(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                ContextRegister<?> register1 = contextRegisterMap.get(o1);
                ContextRegister<?> register2 = contextRegisterMap.get(o2);
                return register1.getOrder() - register2.getOrder();
            }
        });
        ContextAccess contextAccess = new ContextAccess(load(sessionId, agentId),
                contextList, contextMap);
        if (!CollectionUtils.isEmpty(bindData)) {
            for (Map.Entry<String, Object> entry : bindData.entrySet()) {
                if (entry.getValue() != null) {
                    contextAccess.contextData().setData(entry.getKey(), entry.getValue());
                }
            }
        }
        return contextAccess;
    }

    public void clearAllContext(String sessionId, String agentId) {
        clearContext(sessionId, agentId, ContextStoreType.values());
    }

    public void clearContext(String sessionId, String agentId, ContextStoreType... storeType) {
        CurrentId currentId = CurrentId.ofContext(sessionId, agentId);
        for (ContextStoreType type : storeType) {
            clearContext(currentId, type);
        }
    }

    private void clearContext(CurrentId currentId, ContextStoreType contextStoreType) {
        ContextStore contextStore = contextStoreService.getContextStore(contextStoreType);
        String id = contextStoreType.buildId(currentId);
        if (StringUtils.hasText(id)) {
            contextStore.remove(id);
        }
    }

    private ContextData load(String sessionId, String agentId) {
        Map<String, Object> dataMap = new HashMap<>();
        CurrentId currentId = CurrentId.ofContext(sessionId, agentId);
        for (ContextStore contextStore : contextStoreService.getAllContextStores()) {
            String id = contextStore.type().buildId(currentId);
            Map<String, String> data = contextStore.getContextData(id);
            if (CollectionUtils.isEmpty(data)) {
                continue;
            }
            dataMap.putAll(data);
        }
        return ContextData.ofMap(dataMap);
    }

    private void saveData(CurrentId currentId, Map<ContextStoreType, Map<String, String>> dataMap) {
        for (ContextStore contextStore : contextStoreService.getAllContextStores()) {
            Map<String, String> data = dataMap.get(contextStore.type());
            String id = contextStore.type().buildId(currentId);
            if (CollectionUtils.isEmpty(data)) {
                continue;
            }
            //获取旧数据，避免覆盖未使用的数据
            Map<String, String> oldData = contextStore.getContextData(id);
            Map<String, String> savedData = new HashMap<>(data.size());
            if (CollectionUtils.isEmpty(oldData)) {
                savedData.putAll(data);
            } else {
                savedData.putAll(oldData);
                savedData.putAll(data);
            }
            contextStore.save(id, savedData, contextStore.type().defaultDuration());
        }
    }

    public void saveContext(CurrentId currentId, ContextAccess contextAccess,
                            boolean taskFinish) {
        try {
            Map<ContextStoreType, Map<String, String>> dataMap = new HashMap<>();
            ContextData contextData = contextAccess.contextData();
            for (Class<? extends IContext> context : contextAccess.keys()) {
                ContextRegister<? extends IContext> contextRegister = getContextRegister(context);
                ContextStoreType storeType = contextRegister.lifecycle().getStoreType();
                if (contextRegister.lifecycle().checkPersistence(taskFinish)) {
                    String data = contextData.readAsString(contextRegister.getKey());
                    dataMap.computeIfAbsent(storeType, k -> new HashMap<>())
                            .put(contextRegister.getKey(), data);
                }
            }
            saveData(currentId, dataMap);
        } catch (Exception e) {
            log.error("【保存上下文】 发生异常,currentId={}", currentId, e);
        }
    }

    public void clearNodeContext(CurrentId currentId, ContextAccess contextAccess) {
        try {
            for (Class<? extends IContext> context : contextAccess.keys()) {
                ContextRegister<? extends IContext> contextRegister = getContextRegister(context);
                if (contextRegister == null) {
                    log.warn("【清除节点上下文】未注册上下文, key: {}", context);
                    continue;
                }
                if (ContextLifecycle.TASK_NODE.equals(contextRegister.lifecycle())) {
                    contextAccess.removeData(context);
                }
            }
        } catch (Exception e) {
            log.error("【清除节点上下文】发生异常,currentId={}", currentId, e);
        }
    }

    public ContextRegister<? extends IContext> getContextRegister(Class<? extends IContext> context) {
        return contextRegisterMap.get(context);
    }

    public ContextRegister<? extends IContext> getContextRegister(String key) {
        return contextKeyRegisterMap.get(key);
    }

    public void onSessionUpdate(SessionStatus sessionStatus, CurrentId currentId, ContextAccess contextAccess,
                                InputParams inputParams) {
        try {
            List<ContextRegister<?>> buildList = new ArrayList<>();
            for (Class<?> context : contextAccess.keys()) {
                ContextRegister<?> contextRegister = contextRegisterMap.get(context);
                if (contextRegister == null) {
                    log.warn("【会话更新】未注册上下文, key: {}, currentId: {}, sessionStatus: {}",
                            context, currentId, sessionStatus);
                    continue;
                }
                if (contextRegister.registerTime().checkRegister(sessionStatus)) {
                    buildList.add(contextRegister);
                } else {
                    contextRegister.onSessionUpdate(sessionStatus, currentId, contextAccess);
                }
            }
            buildContext(currentId, contextAccess, buildList, inputParams, null);
        } catch (Exception e) {
            log.error("【会话更新】发生异常, currentId: {}, sessionStatus: {}",
                    currentId, sessionStatus, e);
        }
    }

    public void onOperateUpdate(OperateStatus operateStatus, CurrentId currentId, ContextAccess contextAccess,
                                InputParams inputParams) {
        try {
            List<ContextRegister<?>> buildList = new ArrayList<>();
            for (Class<?> context : contextAccess.keys()) {
                ContextRegister<?> contextRegister = contextRegisterMap.get(context);
                if (contextRegister == null) {
                    log.warn("【操作更新】未注册上下文, key: {}, currentId: {}, operateStatus: {}",
                            context, currentId, operateStatus);
                    continue;
                }
                if (contextRegister.registerTime().checkRegister(operateStatus)) {
                    buildList.add(contextRegister);
                } else {
                    contextRegister.onOperateUpdate(operateStatus, currentId, contextAccess);
                }
            }
            buildContext(currentId, contextAccess, buildList, inputParams, ContextData.of());
        } catch (Exception e) {
            log.error("【操作更新】发生异常, currentId: {}, operateStatus: {}",
                    currentId, operateStatus, e);
        }
    }

    public void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId, ContextAccess contextAccess,
                             InputParams inputParams) {
        try {
            List<ContextRegister<?>> buildList = new ArrayList<>();
            for (Class<?> context : contextAccess.keys()) {
                ContextRegister<?> contextRegister = contextRegisterMap.get(context);
                if (contextRegister == null) {
                    log.warn("【任务更新】未注册上下文, key: {}, currentId: {}, taskExecStatus: {}",
                            context, currentId, taskExecStatus);
                    continue;
                }
                if (contextRegister.registerTime().checkRegister(taskExecStatus)) {
                    buildList.add(contextRegister);
                } else {
                    contextRegister.onTaskUpdate(taskExecStatus, currentId, contextAccess);
                }
            }
            buildContext(currentId, contextAccess, buildList, inputParams, ContextData.of());
        } catch (Exception e) {
            log.error("【任务更新】发生异常, currentId: {}, taskExecStatus: {}",
                    currentId, taskExecStatus, e);
        }
    }

    public void onTaskNodeUpdate(TaskNodeStatus taskNodeStatus, CurrentId currentId, ContextAccess contextAccess,
                                 InputParams inputParams) {
        try {
            List<ContextRegister<?>> buildList = new ArrayList<>();
            for (Class<?> context : contextAccess.keys()) {
                ContextRegister<?> contextRegister = contextRegisterMap.get(context);
                if (contextRegister == null) {
                    log.warn("【任务节点更新】未注册上下文, key: {}, currentId: {}, taskNodeStatus: {}",
                            context, currentId, taskNodeStatus);
                    continue;
                }
                if (contextRegister.registerTime().checkRegister(taskNodeStatus)) {
                    buildList.add(contextRegister);
                } else {
                    contextRegister.onTaskNodeUpdate(taskNodeStatus, currentId, contextAccess);
                }
            }
            buildContext(currentId, contextAccess, buildList, inputParams, ContextData.of());
        } catch (Exception e) {
            log.error("【任务节点更新】发生异常, currentId: {}, taskNodeStatus: {}",
                    currentId, taskNodeStatus, e);
        }
    }

    public List<Class<? extends IContext>> onContextBuild(CurrentId currentId, ContextAccess contextAccess, InputParams inputParams, ContextData extendParams) {
        List<ContextRegister<? extends IContext>> buildList = new ArrayList<>();
        List<Class<? extends IContext>> buildContexts = new ArrayList<>();
        for (Class<? extends IContext> context : contextAccess.keys()) {
            ContextRegister<? extends IContext> contextRegister = contextRegisterMap.get(context);
            if (contextRegister == null) {
                log.warn("【上下文构建】未注册上下文, key: {}, currentId: {}",
                        context, currentId);
                continue;
            }
            if (contextRegister.registerTime().contextBuild()) {
                buildList.add(contextRegister);
                buildContexts.add(context);
            }
        }
        buildContext(currentId, contextAccess, buildList, inputParams, extendParams);
        return buildContexts;
    }

    private void buildContext(CurrentId currentId, ContextAccess contextAccess,
                              List<ContextRegister<?>> buildList, InputParams inputParams, ContextData extendParams) {
        if (buildList.isEmpty()) {
            return;
        }
        List<CompletableFuture<Void>> futures = new ArrayList<>(buildList.size());
        KContext kContext = KContextHolder.get();
        int index = 0;
        for (ContextRegister<?> contextRegister : buildList) {
            String subId = index + "";
            index++;
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    if (kContext != null) {
                        KContextHolder.set(kContext.copy());
                    }
                    CurrentIdHolder.setCurrentId(currentId.creteSub(subId));
                    log.debug("【上下文构建】开始构建: {}", contextRegister.getKey());
                    contextRegister.build(currentId, contextAccess, inputParams, extendParams);
                    log.debug("【上下文构建】构建完成: {}", contextRegister.getKey());
                } catch (Exception e) {
                    log.error("【上下文构建】发生异常, currentId: {}, key: {}",
                            currentId, contextRegister.getKey(), e);
                } finally {
                    CurrentIdHolder.removeCurrentId();
                    KContextHolder.clear();
                }
            }, executorService));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

}
