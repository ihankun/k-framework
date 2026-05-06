package io.hankun.framework.ai.mem.hsitory;

import io.hankun.framework.ai.mem.entity.MemData;
import io.hankun.framework.ai.mem.entity.MemMeta;
import io.hankun.framework.ai.mem.entity.MemType;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: MemHistoryService
 * @createAt: 2025/12/4 14:11
 * @author: hankun
 */
@Component
public class MemHistoryService {

    private final MemHistoryRepository memHistoryRepository;

    public MemHistoryService(MemHistoryRepository memHistoryRepository) {
        this.memHistoryRepository = memHistoryRepository;
    }

    public void save(MemData memData) {
        memHistoryRepository.save(convert(memData));
    }

    public List<MemData> loadFact(String uniqueId, String tag, int max) {
        List<MemHistory> memHistories = memHistoryRepository.searchBefore(uniqueId, MemType.FACT.getCode(), tag, null, true,
                max, Sort.Direction.ASC);
        return memHistories.stream().map(MemHistoryService::convert).toList();
    }

    public static MemData convert(MemHistory memHistory) {
        MemMeta memMeta = MemMeta.build(memHistory.getUniqueId(), memHistory.getTag(), memHistory.getType(),
                memHistory.getMeta(), memHistory.getSaveTime());
        return new MemData(memHistory.getMemId(), memHistory.getContent(), memMeta);
    }

    public static MemHistory convert(MemData memData) {
        MemHistory memHistory = new MemHistory();
        memHistory.setId(new ObjectId());
        memHistory.setMemId(memData.id());
        memHistory.setUniqueId(memData.meta().getUniqueId());
        memHistory.setSaveTime(memData.meta().getCreateTime());
        memHistory.setContent(memData.context());
        memHistory.setTag(memData.meta().getTag());
        memHistory.setType(memData.meta().getType());
        memHistory.setMeta(memData.meta().getExtend());
        return memHistory;
    }
}
