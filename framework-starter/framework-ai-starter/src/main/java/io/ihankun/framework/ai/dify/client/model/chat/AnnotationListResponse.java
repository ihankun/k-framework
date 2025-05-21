package io.ihankun.framework.ai.dify.client.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 标注列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationListResponse {

    /**
     * 返回条数
     */
    private Integer limit;

    /**
     * 是否存在下一页
     */
    private Boolean hasMore;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 标注列表
     */
    private List<Annotation> data;
}
