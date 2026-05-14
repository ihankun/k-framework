package io.hankun.framework.ai.mcp.app.nacos.v1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.util.DateUtils;
import io.hankun.framework.ai.common.entity.ServiceInfo;
import io.hankun.framework.ai.common.http.KHttpClient;
import io.hankun.framework.ai.common.util.PathUtil;
import io.hankun.framework.ai.mcp.app.config.KAiHttpConfig;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KV1Client
 * @createAt: 2025/6/5 16:57
 * @author: hankun
 */
@Component
public class KV1Client {

    private final KHttpClient kHttpClient;

    public KV1Client(KAiHttpConfig kAiHttpConfig) {
        this.kHttpClient = new KHttpClient(kAiHttpConfig.getNacos());
    }

    public List<ServiceInfo> listServices(String nacosAddress,
                                          String serviceName,
                                          String clusterName,
                                          String groupName,
                                          String namespaceId) {
        String path = "nacos/v1/ns/instance/list?serviceName=" + serviceName +
                "&clusterName=" + clusterName +
                "&groupName=" + groupName +
                "&namespaceId=" + namespaceId;
        Request request = kHttpClient.get(PathUtil.mergePath(nacosAddress, path), builder -> {
        });
        String httpResult = kHttpClient.call(request, false).data();
        if (ObjectUtils.isEmpty(httpResult)) {
            return List.of();
        }
        NacosServiceInfo nacosServiceInfo = JSON.parseObject(httpResult, NacosServiceInfo.class);
        if (nacosServiceInfo == null || CollectionUtils.isEmpty(nacosServiceInfo.getHosts())) {
            return List.of();
        }
        List<ServiceInfo> serviceInfos = new ArrayList<>(nacosServiceInfo.getHosts().size());
        for (NacosServiceInfo.HostsDTO host : nacosServiceInfo.getHosts()) {
            if (host.getHealthy() == null || !host.getHealthy()) {
                continue;
            }
            Map<String, String> metadata = host.getMetadata();
            String gray = metadata.get("mark");
            if (gray == null) {
                gray = "";
            }
            String version = metadata.get("version");
            Date date = getDate(metadata);
            ServiceInfo serviceInfo = new ServiceInfo(host.getServiceName(),
                    gray, version, host.getIp(), host.getPort(), date);
            serviceInfos.add(serviceInfo);
        }
        return serviceInfos;
    }

    private static Date getDate(Map<String, String> metadata) {
        String dateString = metadata.get("startup.time");
        if (ObjectUtils.isEmpty(dateString)) {
            return new Date();
        }
        //Thu Jun 05 12:16:18 CST 2025
        return DateUtils.parseDate(dateString);
    }

}
