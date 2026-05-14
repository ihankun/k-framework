package io.hankun.framework.ai.agent.audio;

import io.hankun.framework.ai.agent.audio.asr.KAsrApi;
import io.hankun.framework.ai.agent.audio.asr.entity.AsrType;
import io.hankun.framework.ai.agent.audio.asr.entity.KAsrResult;
import io.hankun.framework.ai.agent.audio.config.KAudioConfig;
import io.hankun.framework.ai.agent.audio.tts.KTtsApi;
import io.hankun.framework.ai.agent.audio.tts.cache.AudioCacheService;
import io.hankun.framework.ai.agent.audio.tts.entity.KAudioFormat;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

/**
 * @description:
 * @className: KAudioService
 * @createAt: 2025/7/23 09:41
 * @author: hankun
 */
@Slf4j
@Component
public class KAudioService {


    private final KAudioConfig kAudioConfig;

    private final AudioCacheService audioCacheService;

    public KAudioService(KAudioConfig kAudioConfig,
                         AudioCacheService audioCacheService) {
        this.kAudioConfig = kAudioConfig;
        this.audioCacheService = audioCacheService;
    }


    public Flux<KAudioData> tts(String conversationId, Flux<String> textFlux) {
        textFlux = textFlux.onErrorComplete();
        textFlux = textFlux.filter(StringUtils::hasText);
        return getTtsApi(conversationId).process(textFlux);
    }

    public KTtsApi getTtsApi(String conversationId, TtsType ttsType) {
        return ttsType.buildAudioApi(kAudioConfig, conversationId, audioCacheService);
    }

    public KTtsApi getTtsApi(String conversationId) {
        TtsType ttsType = TtsType.getByCode(kAudioConfig.getType());
        if (ttsType == null) {
            throw new RuntimeException("未找到对应的tts");
        }
        log.info("开始音频转换，type={},format={}", ttsType.getCode(), ttsType.buildAudioFormat(kAudioConfig));
        return ttsType.buildAudioApi(kAudioConfig, conversationId, audioCacheService);
    }

    public KAsrApi getAsrApi(String conversationId, String scene, AsrType asrType) {
        return asrType.buildAsrApi(kAudioConfig, conversationId, scene);
    }

    public KAsrApi getAsrApi(String conversationId, String scene) {
        AsrType asrType = AsrType.getByCode(kAudioConfig.getAsr().getType());
        if (asrType == null) {
            throw new RuntimeException("未找到对应的asr");
        }
        return asrType.buildAsrApi(kAudioConfig, conversationId, scene);
    }

    public Flux<KAsrResult> asr(String conversationId, String scene, Flux<ByteBuffer> audioData) {
        KAsrApi kAsrApi = getAsrApi(conversationId, scene);
        return kAsrApi.process(audioData.onErrorComplete());
    }

    public KAudioFormat buildAudioFormat() {
        TtsType ttsType = TtsType.getByCode(kAudioConfig.getType());
        if (ttsType == null) {
            throw new RuntimeException("未找到对应的tts");
        }
        return ttsType.buildAudioFormat(kAudioConfig);
    }


}
