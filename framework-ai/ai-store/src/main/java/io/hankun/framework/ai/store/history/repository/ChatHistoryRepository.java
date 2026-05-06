package io.hankun.framework.ai.store.history.repository;

import io.hankun.framework.ai.store.history.po.ChatHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: ModelHistoryRepository
 * @createAt: 2025/10/16 19:03
 * @author: hankun
 */
@Component
public class ChatHistoryRepository {

    private final MongoTemplate mongoTemplate;

    public ChatHistoryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public List<ChatHistory> loadChatData(String sessionId) {
        Criteria criteria = new Criteria();
        
        if (sessionId != null && !sessionId.isEmpty()) {
            criteria.and("sessionId").is(sessionId);
        }
        // 根据startTime降序排序
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "startTime"));
        return mongoTemplate.find(query, ChatHistory.class);
    }

}
