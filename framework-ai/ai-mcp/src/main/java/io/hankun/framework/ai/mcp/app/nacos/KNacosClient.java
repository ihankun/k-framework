package io.hankun.framework.ai.mcp.app.nacos;

import io.hankun.framework.ai.core.entity.ServiceInfo;
import io.hankun.framework.ai.mcp.app.config.KAiHttpConfig;
import io.hankun.framework.ai.mcp.app.nacos.v1.KV1Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @description:
 * @className: KNacosClient
 * @createAt: 2025/6/5 16:51
 * @author: hankun
 */
@Slf4j
@Component
public class KNacosClient {

    private final KV1Client KV1Client;

    private final KAiHttpConfig kAiHttpConfig;

    public KNacosClient(KV1Client KV1Client,
                        KAiHttpConfig kAiHttpConfig) {
        this.KV1Client = KV1Client;
        this.kAiHttpConfig = kAiHttpConfig;
    }

    public List<ServiceInfo> listServices(String serviceName,
                                          String clusterName,
                                          String groupName,
                                          String namespaceId) {
        String address = kAiHttpConfig.getNacosAddress();
        if (ObjectUtils.isEmpty(address)) {
            log.error("nacos address is empty");
            return List.of();
        }
        if (ObjectUtils.isEmpty(serviceName)) {
            log.error("serviceName is empty");
            return List.of();
        }
        if (ObjectUtils.isEmpty(clusterName)) {
            clusterName = kAiHttpConfig.getClusterName();
        }
        if (ObjectUtils.isEmpty(groupName)) {
            groupName = kAiHttpConfig.getGroupName();
        }
        if (ObjectUtils.isEmpty(namespaceId)) {
            namespaceId = kAiHttpConfig.getNamespaceId();
        }
        return KV1Client.listServices(address, serviceName, clusterName, groupName, namespaceId);
    }
}
