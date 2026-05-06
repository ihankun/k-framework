package io.hankun.framework.ai.agent.audio.tts.entity;

import io.hankun.framework.ai.agent.audio.config.AudioInfo;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @description:
 * @className: AudioCacheData
 * @createAt: 2025/11/25 11:03
 * @author: hankun
 */
@Data
@Document("audioCacheData")
@CompoundIndex(name = "text_audio_key_index", def = "{'text': 1, 'audioKey': 1}", unique = true)
public class AudioCacheData {
    private ObjectId id;
    private String text;
    private String audioKey;
    private AudioInfo audioInfo;
    private List<byte[]> audio;
}
