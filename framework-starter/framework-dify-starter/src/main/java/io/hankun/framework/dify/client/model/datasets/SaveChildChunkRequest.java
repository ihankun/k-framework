package io.hankun.framework.dify.client.model.datasets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建或者更新子分段请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveChildChunkRequest {
  /**
   * 子分段内容
   */
  private String content;

}
