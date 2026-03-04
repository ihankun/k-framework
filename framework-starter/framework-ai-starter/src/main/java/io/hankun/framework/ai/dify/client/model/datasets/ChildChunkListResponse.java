package io.hankun.framework.ai.dify.client.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 子分段响应
 * 
 * 示例：
 * {
 *    "data": [{
 *      "id": "",
 *      "position": 1,
 *      "document_id": "",
 *      "content": "1",
 *      "answer": "1",
 *      "word_count": 25,
 *      "tokens": 0,
 *      "keywords": ["a"],
 *      "index_node_id": "",
 *      "index_node_hash": "",
 *      "hit_count": 0,
 *      "enabled": true,
 *      "disabled_at": null,
 *      "disabled_by": null,
 *      "status": "completed",
 *      "created_by": "",
 *      "created_at": 1695312007,
 *      "indexing_at": 1695312007,
 *      "completed_at": 1695312007,
 *      "error": null,
 *      "stopped_at": null
 *    }],
 *    "doc_form": "text_model",
 *    "has_more": false,
 *    "limit": 20,
 *    "total": 9,
 *    "page": 1
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildChunkListResponse {

  /**
   * 子分段信息
   */
  private List<ChildChunk> data;

  /**
   * 文档类型
   */
  private String docForm;

  /**
   * 是否还有更多
   */
  private Boolean hasMore;

  /**
   * 每页数量
   */
  private Integer limit;

  /**
   * 总数
   */
  private Integer total;

  /**
   * 页码
   */
  private Integer page;

  /**
   * 子分段信息
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChildChunk {

    /**
     * 子分段ID
     */
    private String id;

    /**
     * 位置
     */
    private Integer position;

    /**
     * 文档ID
     */
    private String documentId;

    /**
     * 内容
     */
    private String content;

    /**
     * 答案
     */
    private String answer;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * Token 数
     */
    private Integer tokens;

    /**
     * 关键词
     * 
     */
     private List<String> keywords;

    /**
     * 索引节点ID
     */
    private String indexNodeId;

    /**
     * 索引节点哈希
     */
    private String indexNodeHash;

    /**
     * 命中数
     */
    private Integer hitCount;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 禁用时间
     */
    private Long disabledAt;

    /**
     * 禁用者
     */
    private String disabledBy;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Long createdAt;

    /**
     * 索引时间
     */
    private Long indexingAt;

    /**
     * 完成时间
     */
    private Long completedAt;

    /**
     * 错误
     */
    private String error;

    /**
     * 停止时间
     */
    private Long stoppedAt;
  }
}
