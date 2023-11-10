package io.ihankun.framework.spring.server.limit.engine;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hankun
 */
@Data
@AllArgsConstructor
public class RateElement {

    /**
     * 限流资源
     */
    private String source;

    /**
     * 所属域名
     */
    private String domain;

    /**
     * 所属用户ID
     */
    private String userId;
}
