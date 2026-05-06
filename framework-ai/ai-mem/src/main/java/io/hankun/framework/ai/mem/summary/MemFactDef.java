package io.hankun.framework.ai.mem.summary;

import io.hankun.framework.ai.mem.entity.MemData;
import io.hankun.framework.ai.mem.entity.MemMeta;
import io.hankun.framework.ai.mem.entity.MemType;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MemFactDef
 * @createAt: 2025/12/5 15:40
 * @author: hankun
 */
public interface MemFactDef {

    String tag();

    List<MemData> loadFactAfter(String uniqueId, String tag, Date startTime);

    List<MemData> loadFactBefore(String uniqueId, String tag, int max, Date endTime);

    default MemData build( String uniqueId, String tag, Date time, String content) {
        return new MemData(new ObjectId().toString(), content, MemMeta.build(uniqueId, tag, MemType.FACT.getCode(), Map.of(), time));
    }
}
