package io.hankun.framework.ai.agent.audio.tts.cache;

import io.hankun.framework.ai.agent.audio.tts.entity.AudioCacheData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: AudioCacheRepository
 * @createAt: 2025/11/25 11:07
 * @author: hankun
 */
@Component
public class AudioCacheRepository {

    private final MongoTemplate mongoTemplate;

    public AudioCacheRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public AudioCacheData getCache(String key, String audioKey) {
        Criteria criteria = Criteria.where("text").is(key);
        criteria.and("audioKey").is(audioKey);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, AudioCacheData.class);
    }

    public void setCache(AudioCacheData value) {
        Criteria criteria = Criteria.where("text").is(value.getText())
                .and("audioKey").is(value.getAudioKey());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.setOnInsert("audioInfo", value.getAudioInfo());
        update.setOnInsert("audio", value.getAudio());
        mongoTemplate.upsert(query, update, AudioCacheData.class);
    }
}
