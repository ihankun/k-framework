package io.hankun.framework.ai.mcp.app.nacos.v1;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: NacosServiceInfo
 * @createAt: 2025/6/5 16:52
 * @author: hankun
 */
@NoArgsConstructor
@Data
public class NacosServiceInfo {

    @JSONField(name = "hosts")
    private List<HostsDTO> hosts;
    @JSONField(name = "dom")
    private String dom;
    @JSONField(name = "name")
    private String name;
    @JSONField(name = "cacheMillis")
    private Integer cacheMillis;
    @JSONField(name = "lastRefTime")
    private Long lastRefTime;
    @JSONField(name = "checksum")
    private String checksum;
    @JSONField(name = "useSpecifiedURL")
    private Boolean useSpecifiedURL;
    @JSONField(name = "clusters")
    private String clusters;
    @JSONField(name = "env")
    private String env;

    @NoArgsConstructor
    @Data
    public static class HostsDTO {
        @JSONField(name = "ip")
        private String ip;
        @JSONField(name = "port")
        private Integer port;
        @JSONField(name = "valid")
        private Boolean valid;
        @JSONField(name = "healthy")
        private Boolean healthy;
        @JSONField(name = "marked")
        private Boolean marked;
        @JSONField(name = "instanceId")
        private String instanceId;
        @JSONField(name = "metadata")
        private Map<String, String> metadata;
        @JSONField(name = "enabled")
        private Boolean enabled;
        @JSONField(name = "weight")
        private Double weight;
        @JSONField(name = "clusterName")
        private String clusterName;
        @JSONField(name = "serviceName")
        private String serviceName;
        @JSONField(name = "ephemeral")
        private Boolean ephemeral;

    }
}
