package io.hankun.framework.ai.store.history.repository;

import io.hankun.framework.ai.store.history.po.ModelHistory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description:
 * @className: ModelHistoryRepository
 * @createAt: 2025/10/16 19:03
 * @author: hankun
 */
@Component
public class ModelHistoryRepository {

    private final MongoTemplate mongoTemplate;

    public ModelHistoryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void save(List<ModelHistory> modelHistoryData) {
        if (CollectionUtils.isEmpty(modelHistoryData)) {
            return;
        }
        mongoTemplate.insertAll(modelHistoryData);
    }

    public List<ModelHistory> loadModelData(String sessionId, String messageId, String type) {
        Criteria criteria = new Criteria();
        
        if (sessionId != null && !sessionId.isEmpty()) {
            criteria.and("sessionId").is(sessionId);
        }
        
        if (messageId != null && !messageId.isEmpty()) {
            criteria.and("messageId").is(messageId);
        }
        
        if (type != null && !type.isEmpty()) {
            criteria.and("type").is(type);
        }
        
        Query query = new Query(criteria);
        return mongoTemplate.find(query, ModelHistory.class);
    }

}
