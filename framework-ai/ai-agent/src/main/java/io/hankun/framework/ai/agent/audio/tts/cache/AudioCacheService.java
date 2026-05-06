package io.hankun.framework.ai.agent.audio.tts.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.hankun.framework.ai.agent.audio.config.AudioInfo;
import io.hankun.framework.ai.agent.audio.config.KAudioConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.AudioCacheData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Base64;
import java.util.List;

/**
 * @description:
 * @className: AudioCacheService
 * @createAt: 2025/11/25 10:37
 * @author: hankun
 */
@Slf4j
@Component
public class AudioCacheService {

    private static final int MAX_SIZE = 30;

    private final Cache<String, List<String>> cached;

    private final AudioCacheRepository audioCacheRepository;

    private final KAudioConfig kAudioConfig;

    public AudioCacheService(AudioCacheRepository audioCacheRepository,
                             KAudioConfig kAudioConfig) {
        this.audioCacheRepository = audioCacheRepository;
        this.kAudioConfig = kAudioConfig;
        this.cached = Caffeine.newBuilder()
                .maximumSize(500)
                .build();
    }

    private List<String> loadFromDb(String key, AudioInfo info) {
        if (info == null) {
            return List.of();
        }
        if (key.length() > MAX_SIZE) {
            log.debug("音频缓存长度过长, 忽略缓存, key={}, audioKey={}", key, info);
            return List.of();
        }
        AudioCacheData audioCacheData = audioCacheRepository.getCache(key, info.toString());
        if (audioCacheData == null) {
            log.debug("音频缓存不存在, key={}, audioKey={}", key, info);
            return null;
        }
        List<byte[]> audio = audioCacheData.getAudio();
        return audio.stream().map(bytes -> Base64.getEncoder().encodeToString(bytes)).toList();
    }

    public List<String> getCache(String key, AudioInfo info) {
        if (!kAudioConfig.getEnableCache()) {
            return List.of();
        }
        String audioKey = info.toString();
        String cacheKey = key + ":" + audioKey;
        return cached.get(cacheKey, cacheKey1 -> {
            log.debug("本地缓存未命中, 从数据库加载缓存, key={}, audioKey={}", key, audioKey);
            return loadFromDb(key, info);
        });
    }

    public void setCache(String key, AudioInfo info, List<String> value) {
        if (key == null) {
            return;
        }
        if (key.length() > MAX_SIZE) {
            return;
        }
        if (CollectionUtils.isEmpty(value)) {
            log.debug("尝试存储空音频缓存, key={}, audioKey={} - 跳过缓存", key, info.toString());
            return;
        }
        log.debug("存储音频缓存, key={}, audioKey={}, 缓存大小={}", key, info.toString(), value.size());
        cached.put(key, value);
        AudioCacheData audioCacheData = new AudioCacheData();
        audioCacheData.setText(key);
        audioCacheData.setAudioKey(info.toString());
        audioCacheData.setAudioInfo(info);
        audioCacheData.setAudio(value.stream().map(Base64.getDecoder()::decode).toList());
        audioCacheRepository.setCache(audioCacheData);
    }
}
