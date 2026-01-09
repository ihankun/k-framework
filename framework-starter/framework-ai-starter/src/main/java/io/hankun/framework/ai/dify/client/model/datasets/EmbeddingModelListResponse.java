package io.hankun.framework.ai.dify.client.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 嵌入模型列表响应
 *
 * @author zhangriguang
 * @date 2025-05-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingModelListResponse {
    /**
     * 嵌入模型列表
     */
    private List<EmbeddingModel> data;

    /**
     * 嵌入模型
     *
     * @author zhangriguang
     * @date 2025-05-13
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbeddingModel {
        /**
         * 元数据提供者
         */
        private String provider;

        /**
         * 标签
         */
        private Language label;

        /**
         * 小图标
         */
        private Language iconSmall;

        /**
         * 大图标
         */
        private Language iconLarge;
        /**
         * 状态
         */
        private String status;
        /**
         * 模型列表
         */
        private List<Model> models;
    }

    /**
     * 语言
     *
     * @author zhangriguang
     * @date 2025-05-13
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Language {
        /**
         * 中文
         */
        private String zhHans;

        /**
         * 英文
         */
        private String enUS;
    }

    /**
     * 模型
     *
     * @author zhangriguang
     * @date 2025-05-13
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Model {
        /**
         * 模型名称
         */
        private String model;

        /**
         * 标签
         */
        private Language label;

        /**
         * 模型类型
         */
        private String modelType;

        /**
         * 特征
         */
        private String features;
        /**
         * 获取方式
         */
        private String fetchFrom;
        /**
         * 模型属性
         */
        private ModelProperties modelProperties;
        /**
         * 弃用
         */
        private String deprecated;
        /**
         * 状态
         */
        private String status;
        /**
         * 负载均衡
         */
        private String loadBalancingEnabled;
    }

    /**
     * 模型属性
     *
     * @author zhangriguang
     * @date 2025-05-13
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelProperties {
        /**
         * 上下文大小
         */
        private Long contextSize;
    }
}
