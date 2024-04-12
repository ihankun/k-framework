package io.ihankun.framework.spring.server.nacos.properties;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author hankun
 */
@NoArgsConstructor
@Data
public class NacosNamespaceInfo {

    private Integer code;

    private List<DataDTO> data;

    private Object message;

    @NoArgsConstructor
    @Data
    public static class DataDTO {

        private String namespace;

        private String namespaceShowName;

        private Integer quota;

        private Integer configCount;

        private Integer type;
    }
}
