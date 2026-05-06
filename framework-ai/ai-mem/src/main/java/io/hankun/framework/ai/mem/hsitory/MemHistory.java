package io.hankun.framework.ai.mem.hsitory;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @className: MemHistory
 * @createAt: 2025/12/4 14:45
 * @author: hankun
 */
@Data
@Document("memHistory")
public class  MemHistory {
    @Id
    private ObjectId id;
    @Indexed
    private String memId;
    @Indexed
    private String uniqueId;
    @Indexed
    private Date saveTime;
    private String content;
    private String tag;
    private String type;
    private Map<String, Object> meta;
}
