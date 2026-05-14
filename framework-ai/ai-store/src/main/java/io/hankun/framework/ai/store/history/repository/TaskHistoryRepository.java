package io.hankun.framework.ai.store.history.repository;

import io.hankun.framework.ai.core.record.ChatRecord;
import io.hankun.framework.ai.core.record.TaskRecord;
import io.hankun.framework.ai.store.history.po.ChatHistory;
import io.hankun.framework.ai.store.history.po.SessionInfo;
import io.hankun.framework.ai.store.history.po.TaskHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @description:
 * @className: TaskHistoryRepository
 * @createAt: 2025/10/16 17:59
 * @author: hankun
 */
@Component
public class TaskHistoryRepository {

    private final MongoTemplate mongoTemplate;

    public TaskHistoryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void save(ChatRecord chatRecord) {
        mongoTemplate.save(ChatHistory.of(chatRecord));
        updateSessionInfo(chatRecord);
    }

    public void save(Collection<TaskRecord> taskRecords) {
        if (CollectionUtils.isEmpty(taskRecords)) {
            return;
        }
        List<TaskHistory> taskHistory = new ArrayList<>(taskRecords.size());
        for (TaskRecord taskRecord : taskRecords) {
            taskHistory.add(TaskHistory.of(taskRecord));
        }
        mongoTemplate.insertAll(taskHistory);
    }

    public void updateSessionInfo(ChatRecord chatRecord) {
        Criteria criteria = Criteria.where("id").is(chatRecord.getSessionId());
        Query query = new Query(criteria);
        Update update = new Update()
                .setOnInsert("id", chatRecord.getSessionId())
                .setOnInsert("userKey", chatRecord.getUserKey())
                .setOnInsert("name", chatRecord.getUserMessage())
                .set("lastUpdateTime", chatRecord.getCreateTime());
        mongoTemplate.upsert(query, update, SessionInfo.class);
    }

    public List<ChatRecord> loadLocalHistory(String sessionId, int count) {

        List<ChatHistory> taskHistories = fetchBefore(sessionId, null, true, count);
        taskHistories.sort(Comparator.comparing(ChatHistory::getId));
        List<ChatRecord> chatRecords = new ArrayList<>(taskHistories.size());
        for (ChatHistory chatHistory : taskHistories) {
            chatRecords.add(ChatHistory.toChatRecord(chatHistory));
        }
        return chatRecords;
    }

    public SessionInfo getSessionInfo(String sessionId) {
        Criteria criteria = Criteria.where("id").is(sessionId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, SessionInfo.class);
    }

    public List<SessionInfo> getSessionByUserKey(String userKey) {
        Criteria criteria = Criteria.where("userKey").is(userKey);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, SessionInfo.class);
    }


    public List<ChatHistory> fetchBefore(String sessionId, String cursor, boolean inclusive, int maxCount, String... fields) {
        Criteria criteria = Criteria.where("sessionId").is(sessionId);
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, "id")).limit(maxCount);
        if (cursor != null) {
            if (inclusive) {
                criteria.and("id").lte(cursor);
            } else {
                criteria.and("id").lt(cursor);
            }
        }
        if (fields != null && fields.length > 0) {
            query.fields().include(fields);
        }
        return mongoTemplate.find(query, ChatHistory.class);
    }

    public List<ChatHistory> fetchTaskHistory(String sessionId, String taskId) {
        Criteria criteria = Criteria.where("sessionId").is(sessionId).and("taskId").is(taskId);
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "id"));
        return mongoTemplate.find(query, ChatHistory.class);
    }
}
