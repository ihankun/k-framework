package io.hankun.framework.ai.dify.client.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 子分段响应
 * 
 * 示例：
 * {
 *    "data": {
 *      "id": "",
 *      "segment_id": "",
 *      "content": "子分段内容",
 *      "word_count": 25,
 *      "tokens": 0,
 *      "index_node_id": "",
 *      "index_node_hash": "",
 *      "status": "completed",
 *      "created_by": "",
 *      "created_at": 1695312007,
 *      "indexing_at": 1695312007,
 *      "completed_at": 1695312007,
 *      "error": null,
 *      "stopped_at": null
 *    }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildChunkResponse {

  /**
   * 子分段信息
   */
  private ChildChunkInfo data;


  /**
   * 子分段信息
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChildChunkInfo {

    /**
     * 子分段ID
     */
    private String id;

    /**
     * 分段ID
     */
    private String segmentId;

    /**
     * 子分段内容
     */
    private String content;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * Token 数
     */
    private Integer tokens;

    /**
     * 索引节点ID
     */
    private String indexNodeId;

    /** 
     * 索引节点哈希
     */
    private String indexNodeHash;

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
