package io.hankun.framework.ai.store.history.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @description:
 * @className: SessionInfo
 * @createAt: 2025/10/16 17:04
 * @author: hankun
 */
@Document(collection = "sessionInfo")
@Data
public class SessionInfo {

    @Id
    private String id;

    private String userKey;

    private String name;

    private Date lastUpdateTime;
}
