package io.hankun.framework.ai.store.memory.shortTerm;

import io.hankun.framework.ai.core.redis.KRedisHolder;
import io.hankun.framework.ai.store.config.KAiStoreConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KRedisChatMemoryRepository
 * @createAt: 2025/6/5 08:56
 * @author: hankun
 */
@Slf4j
@Component
public class KRedisChatMemoryRepository implements ChatMemoryRepository {

    private final RedissonClient redissonClient;

    private final KAiStoreConfig kAiStoreConfig;

    private final KRedisHolder kRedisHolder;

    public KRedisChatMemoryRepository(KRedisHolder kRedisHolder, KAiStoreConfig kAiStoreConfig) {
        this.redissonClient = kRedisHolder.getRedissonClient();
        this.kRedisHolder = kRedisHolder;
        this.kAiStoreConfig = kAiStoreConfig;
    }

    private RList<String> getMessages(String conversationId) {
        RList<String> messagesList = redissonClient.getList(getConversationKey(conversationId));
        messagesList.expire(Duration.ofHours(kAiStoreConfig.getMemoryExpireTimeHours()));
        return messagesList;
    }

    private String getConversationKey(String conversationId) {
        return getPrefix() + conversationId;
    }


    private String getPrefix() {
        return kRedisHolder.getKeyPrefix("chat-memory");
    }


    public Map<String, Long> showExpire() {
        Iterable<String> keys = scan();
        Map<String, Long> result = new HashMap<>();
        for (String key : keys) {
            long expire = redissonClient.getList(key).remainTimeToLive();
            if (expire < 0) {
                redissonClient.getList(key).expire(Duration.ofHours(kAiStoreConfig.getMemoryExpireTimeHours()));
            }
            result.put(key, expire);
        }
        return result;
    }


    @NotNull
    @Override
    public List<String> findConversationIds() {
        Iterable<String> keys = scan();
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            key = getConversationId(key);
            result.add(key);
        }
        return result;
    }

    @NotNull
    public String getConversationId(String key) {
        return key.substring(getPrefix().length());
    }

    public Iterable<String> scan() {
        return redissonClient.getKeys().
                getKeysByPattern(getPrefix() + "*");
    }

    @NotNull
    @Override
    public List<Message> findByConversationId(@NotNull String conversationId) {
        return getMessages(conversationId).stream()
                .map(MessageConvertor::convert)
                .toList();
    }

    @Override
    public void saveAll(@NotNull String conversationId, @NotNull List<Message> messages) {
        RList<String> messagesList = getMessages(conversationId);
        messagesList.clear();
        List<String> newMessages = new ArrayList<>(messages.size());
        for (Message message : messages) {
            newMessages.add(MessageConvertor.convert(message));
        }
        messagesList.addAll(newMessages);
    }

    @Override
    public void deleteByConversationId(@NotNull String conversationId) {
        redissonClient.getList(getConversationKey(conversationId)).clear();
    }

    public void clear() {
        Iterable<String> keys = scan();
        for (String key : keys) {
            redissonClient.getList(key).delete();
        }
    }
}
