package io.hankun.framework.ai.mem.hsitory;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @className: MemHistoryRepository
 * @createAt: 2025/12/4 14:12
 * @author: hankun
 */
@Component
public class MemHistoryRepository {

    private final MongoTemplate mongoTemplate;

    public MemHistoryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void save(MemHistory memHistory) {
        //upsert
        Criteria criteria = Criteria.where("uniqueId").is(memHistory.getUniqueId())
                .and("type").is(memHistory.getType())
                .and("tag").is(memHistory.getTag())
                .and("content").is(memHistory.getContent());
        Update update = new Update();
        update.setOnInsert("uniqueId", memHistory.getUniqueId());
        update.setOnInsert("type", memHistory.getType());
        update.setOnInsert("tag", memHistory.getTag());
        update.setOnInsert("memId", memHistory.getMemId());
        update.setOnInsert("saveTime", memHistory.getSaveTime());
        update.setOnInsert("content", memHistory.getContent());
        update.setOnInsert("meta", memHistory.getMeta());
        mongoTemplate.upsert(new Query(criteria), update, MemHistory.class);
    }

    public List<MemHistory> searchBefore(String uniqueId, String type, String tag, Date time,
                                         boolean inclusive,
                                         int max, Sort.Direction direction) {
        Criteria criteria = Criteria.where("uniqueId").is(uniqueId)
                .and("type").is(type)
                .and("tag").is(tag);
        if (time != null) {
            if (inclusive) {
                criteria.and("saveTime").lte(time);
            } else {
                criteria.and("saveTime").lt(time);
            }
        }
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, "id")).limit(max);
        List<MemHistory> memHistories = mongoTemplate.find(query, MemHistory.class);
        if (Sort.Direction.ASC.equals(direction)) {
            memHistories.sort(Comparator.comparing(MemHistory::getSaveTime));
        }
        return memHistories;
    }
}
