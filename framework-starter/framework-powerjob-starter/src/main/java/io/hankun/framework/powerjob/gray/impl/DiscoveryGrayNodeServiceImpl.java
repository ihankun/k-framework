package io.hankun.framework.powerjob.gray.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import io.hankun.framework.powerjob.gray.GrayNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: GrayNodeService 的默认实现，基于 Spring Cloud DiscoveryClient。
 * @fileName: DiscoveryGrayNodeServiceImpl.java
 * @author: hankun
 */

@Slf4j
@Service
public class DiscoveryGrayNodeServiceImpl implements GrayNodeService {

    // Spring Cloud 服务发现客户端
    private final DiscoveryClient discoveryClient;

    // 从配置文件中获取当前应用的名称，例如 "framework-powerjob-worker-example"
    @Value("${spring.application.name}")
    private String currentAppName;

    // 从 PowerJob worker 的配置文件中获取 worker 的端口号
    @Value("${powerjob.worker.akka-port:27777}")
    private int powerjobWorkerPort;

    // 添加这个常量，与监听器中的 Key 保持一致
    private static final String POWERJOB_WORKER_ADDRESS_KEY = "powerjob.worker.address";
    // 标记的 Key
    private static final String MARK_KEY = "mark";
    // 灰度标记的 Value
    private static final String GRAY_MARK_VALUE = "gray";

    public DiscoveryGrayNodeServiceImpl(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<String> getGrayWorkerAddresses() {
        if (discoveryClient == null) {
            log.warn("[framework-powerjob-gray] DiscoveryClient 未注入，无法获取灰度节点列表。");
            return Collections.emptyList();
        }

        log.debug("[framework-powerjob-gray] 开始从服务发现中心获取应用 [{}] 的实例列表...", currentAppName);
        // 1. 获取当前应用的所有在线实例
        List<ServiceInstance> instances = discoveryClient.getInstances(currentAppName);

        if (CollectionUtils.isEmpty(instances)) {
            log.warn("[framework-powerjob-gray] 未找到应用 [{}] 的任何在线实例。", currentAppName);
            return Collections.emptyList();
        }

        log.debug("[framework-powerjob-gray] 找到 {} 个实例，开始过滤灰度节点...", instances.size());

        // 2. 遍历并过滤出灰度实例
        List<String> grayAddresses = instances.stream()
                .filter(this::isGrayInstance)
                .map(this::buildWorkerAddress)
                .collect(Collectors.toList());

        if (!grayAddresses.isEmpty()) {
            log.info("[framework-powerjob-gray] 成功过滤出 {} 个灰度节点: {}", grayAddresses.size(), grayAddresses);
        } else {
            log.info("[framework-powerjob-gray] 在所有在线实例中未发现任何灰度节点。");
        }

        return grayAddresses;
    }

    /**
     * 判断一个服务实例是否为灰度实例。
     */
    private boolean isGrayInstance(ServiceInstance instance) {
        // 3. 检查元数据
        Map<String, String> metadata = instance.getMetadata();
        if (metadata == null) {
            return false;
        }
        // 元数据中 'mark' 键的值是否等于 'gray'
        boolean isGray = GRAY_MARK_VALUE.equals(metadata.get(MARK_KEY));
        if (isGray) {
            log.debug("[framework-powerjob-gray] 实例 [Host: {}, Port: {}] 是灰度节点。", instance.getHost(), instance.getPort());
        }
        return isGray;
    }

    /**
     * 构建 PowerJob worker 需要的地址格式 "IP:PORT"。
     * 优先从 Nacos 实例的元数据中读取 PowerJob Worker 的真实地址。
     * 如果元数据中不存在，则回退到使用 instance.getHost() 的旧逻辑。
     */
    private String buildWorkerAddress(ServiceInstance instance) {

        Map<String, String> metadata = instance.getMetadata();

        // 1. 优先从元数据中获取
        if (metadata != null) {
            String workerAddress = metadata.get(POWERJOB_WORKER_ADDRESS_KEY);
            if (StringUtils.hasText(workerAddress)) {
                log.debug("[framework-powerjob-gray] Instance [Host: {}, Port: {}] found real worker address [{}] from Nacos metadata.",
                        instance.getHost(), instance.getPort(), workerAddress);
                return workerAddress;
            }
        }

        // 2. 如果元数据中没有，则回退到旧逻辑
        String fallbackAddress = instance.getHost() + ":" + powerjobWorkerPort;
        log.warn("[framework-powerjob-gray] Instance [Host: {}, Port: {}] could not find real worker address in metadata. " +
                "Falling back to default address [{}].", instance.getHost(), instance.getPort(), fallbackAddress);

        return fallbackAddress;
    }
}
