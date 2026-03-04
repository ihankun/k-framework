package io.hankun.framework.ai.dify.client.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 元数据请求数据
 *
 * @author zhangriguang
 * @date 2025-05-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationData {
    /**
     * 文档 ID
     */
    private String documentId;

    /**
     * 元数据列表
     */
    private List<Metadata> metadataList ;

    /**
     * 元数据
     *
     * @author zhangriguang
     * @date 2025-05-13
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        /**
         * 元数据 ID
         */
        private String id;

        /**
         * 元数据类型
         */
        private String type;

        /**
         * 元数据名称
         */
        private String name;
    }
}
