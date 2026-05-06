package io.hankun.framework.ai.mcp.app;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.common.entity.HttpResult;
import io.hankun.framework.ai.common.entity.ServiceInfo;
import io.hankun.framework.ai.common.http.KHttpClient;
import io.hankun.framework.ai.common.util.PathUtil;
import io.hankun.framework.ai.mcp.app.config.KAiHttpConfig;
import io.hankun.framework.ai.mcp.app.config.KAiMcpConfig;
import io.hankun.framework.ai.mcp.app.entity.KFunction;
import io.hankun.framework.ai.mcp.app.entity.KFunctionList;
import io.hankun.framework.ai.mcp.app.event.ToolUpdateEvent;
import io.hankun.framework.ai.mcp.app.event.ToolUpdateType;
import io.hankun.framework.ai.mcp.app.nacos.KNacosClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @className: NacosMsunFunctionManager
 * @createAt: 2025/6/5 17:20
 * @author: hankun
 */
@Slf4j
@ConditionalOnProperty(
        prefix = "msun.ai.mcp",
        name = {"registerType"},
        havingValue = "nacos",
        matchIfMissing = true
)
@Component
public class NacosKFunctionManager extends KFunctionManager {

    private final KNacosClient kNacosClient;

    private final KHttpClient kHttpClient;

    private final Map<String, Map<String, KFunctionList>> functionMap = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final ApplicationEventPublisher eventPublisher;

    public NacosKFunctionManager(KNacosClient kNacosClient,
                                 KAiMcpConfig kAiMcpConfig,
                                 KAiHttpConfig kAiHttpConfig,
                                 ApplicationEventPublisher eventPublisher) {
        super(kAiMcpConfig);
        this.kNacosClient = kNacosClient;
        this.kHttpClient = new KHttpClient(kAiHttpConfig.getNacos());
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        fresh();
    }

    @Scheduled(fixedDelay = 1000 * 20)
    private void fresh() {
        lock.lock();
        try {
            for (String serviceName : kAiMcpConfig.getMsunServices()) {
                try {
                    freshService(serviceName);
                } catch (Throwable e) {
                    log.error("刷新服务{}失败", serviceName, e);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void addEvent(KFunctionList kFunctionList) {
        eventPublisher.publishEvent(new ToolUpdateEvent(this, kFunctionList, ToolUpdateType.ADD));
    }

    private void removeEvent(KFunctionList kFunctionList) {
        eventPublisher.publishEvent(new ToolUpdateEvent(this, kFunctionList, ToolUpdateType.REMOVE));
    }

    private void freshService(String serviceName) {
        List<ServiceInfo> serviceInfos = kNacosClient.listServices(serviceName,
                kAiMcpConfig.getClusterName().get(serviceName),
                kAiMcpConfig.getGroupName().get(serviceName),
                kAiMcpConfig.getNamespaceId().get(serviceName));
        Map<String, KFunctionList> functions = functionMap.get(serviceName);
        if (functions == null) {
            functions = new HashMap<>(serviceInfos.size());
        }
        MultiValueMap<String, ServiceInfo> serviceInfoMap = new LinkedMultiValueMap<>();
        for (ServiceInfo serviceInfo : serviceInfos) {
            serviceInfoMap.add(serviceInfo.grayMark(), serviceInfo);
        }
        Map<String, ServiceInfo> updateServiceInfoMap = new HashMap<>();
        Set<String> grayMarks = new HashSet<>(serviceInfos.size());
        for (Map.Entry<String, List<ServiceInfo>> entry : serviceInfoMap.entrySet()) {
            String grayMark = entry.getKey();
            grayMarks.add(grayMark);
            KFunctionList funcList = functions.get(grayMark);
            List<ServiceInfo> dataList = entry.getValue();
            dataList.sort(Comparator.comparing(ServiceInfo::serviceVersion)
                    .thenComparing(ServiceInfo::serviceStartTime));
            if (checkUpdate(funcList, dataList)) {
                ServiceInfo serviceInfo = dataList.getFirst();
                updateServiceInfoMap.put(grayMark, serviceInfo);
                log.info("服务{}的灰度【{}】发生改变，进行刷新", serviceInfo.serviceName(), serviceInfo.grayMark());
            }
        }
        // 删除不存在的灰度信息
        functions.entrySet().removeIf(entry -> {
            boolean remove = !grayMarks.contains(entry.getKey());
            if (remove) {
                log.info("服务{}的灰度【{}】不存在，进行清理", serviceName, entry.getKey());
                removeEvent(entry.getValue());
            }
            return remove;
        });
        //不存在需要更新的灰度标识
        if (updateServiceInfoMap.isEmpty()) {
            log.debug("服务{}未进行刷新", serviceName);
            return;
        }
        for (Map.Entry<String, ServiceInfo> entry : updateServiceInfoMap.entrySet()) {
            ServiceInfo serviceInfo = entry.getValue();
            try {
                String baseUrl = buildFunctionUrl(serviceInfo.ip(), serviceInfo.port(), kAiMcpConfig.getPath().get(serviceName));
                Request request = kHttpClient.get(PathUtil.mergePath(baseUrl, "/function/listFunctions"), builder ->
                {
                });
                HttpResult httpResult = kHttpClient.call(request, true);
                if (!httpResult.success()) {
                    log.error("服务{}的灰度【{}】刷新失败，原因：{}", serviceName, getString(serviceInfo), httpResult.message());
//                    MsunFunctionList empty = new MsunFunctionList();
//                    empty.setServiceName(serviceInfo.serviceName());
//                    empty.setGrayMark(serviceInfo.grayMark());
//                    empty.setVersion(serviceInfo.serviceVersion());
//                    empty.setStartTime(serviceInfo.serviceStartTime());
//                    empty.setFunctions(new ArrayList<>());
//                    MsunFunctionList old = functions.put(serviceInfo.grayMark(), empty);
//                    if (old != null) {
//                        removeEvent(old);
//                    }
//                    addEvent(empty);
                    continue;
                }
                String result = httpResult.data();
                KFunctionList kFunctionList = JSON.parseObject(result, KFunctionList.class);
                kFunctionList.setStartTime(serviceInfo.serviceStartTime());
                for (KFunction function : kFunctionList.getFunctions()) {
                    function.setServiceName(serviceInfo.serviceName());
                    function.setGrayMark(serviceInfo.grayMark());
                }
                KFunctionList old = functions.put(serviceInfo.grayMark(), kFunctionList);
                if (old != null) {
                    removeEvent(old);
                }
                addEvent(kFunctionList);
            } catch (Exception e) {
                log.error("服务{}的灰度【{}】刷新失败", serviceName, serviceInfo.grayMark(), e);
            }
        }
        functionMap.put(serviceName, functions);
        log.debug("服务{}刷新成功", serviceName);
    }

    private boolean checkUpdate(KFunctionList functionList, List<ServiceInfo> serviceInfos) {
        if (functionList == null) {
            return true;
        }
        //信息已经读取了
        //启动时间相同
        //灰度标记相同
        //无需更新
        ServiceInfo serviceInfo = serviceInfos.getFirst();
        return !Objects.equals(serviceInfo.serviceStartTime(), functionList.getStartTime())
                || !Objects.equals(serviceInfo.serviceVersion(), functionList.getVersion());
    }

    private static String getString(ServiceInfo serviceInfo) {
        return serviceInfo.grayMark();
    }

    private String buildFunctionUrl(String ip, Integer port, String path) {
        return PathUtil.mergePath(String.format("http://%s:%s", ip, port), path);
    }

    @Override
    public Map<String, KFunctionList> getFunctionsByServiceName(String serviceName) {
        return functionMap.get(serviceName);
    }
}
