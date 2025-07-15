package io.ihankun.framework.db.limit.element;

import lombok.Data;

import java.util.UUID;

/**
 * @author hankun
 */
@Data
public class RateSnapshotElement {

    /**
     * 唯一ID，因为统一资源可能在同一时刻多次发生，如果单纯依据资源移除会导致统计错误
     */
    private String uuid;

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

    public RateSnapshotElement(String source, String domain, String userId) {
        this.source = source;
        this.domain = domain;
        this.userId = userId;
        this.uuid = UUID.randomUUID().toString();
    }

}
