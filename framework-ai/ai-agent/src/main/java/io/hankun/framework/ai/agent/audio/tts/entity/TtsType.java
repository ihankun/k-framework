package io.hankun.framework.ai.agent.audio.tts.entity;

import io.hankun.framework.ai.agent.audio.config.KAudioConfig;
import io.hankun.framework.ai.agent.audio.tts.KTtsApi;
import io.hankun.framework.ai.agent.audio.tts.cache.AudioCacheService;
import io.hankun.framework.ai.agent.audio.tts.client.AliTtsClient;
import io.hankun.framework.ai.agent.audio.tts.client.KWebsocketTtsClient;
import io.hankun.framework.ai.agent.audio.tts.client.QwenCommitTtsClient;
import io.hankun.framework.ai.agent.audio.tts.client.QwenServerCommitTtsClient;
import io.hankun.framework.ai.agent.audio.tts.impl.FluxAliTtsImpl;
import io.hankun.framework.ai.agent.audio.tts.impl.KTtsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.sound.sampled.AudioFormat;

/**
 * @description:
 * @className: TtsType
 * @createAt: 2025/7/30 16:39
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum TtsType {

    K("k") {
        @Override
        public KTtsApi buildAudioApi(KAudioConfig kAudioConfig, String conversationId, AudioCacheService audioCacheService) {
            TtsConfig ttsConfig = kAudioConfig.getK().toTtsConfig();
            return new KTtsImpl(new KWebsocketTtsClient(ttsConfig), ttsConfig, conversationId,
                    false, audioCacheService);
        }

        @Override
        public KAudioFormat buildAudioFormat(KAudioConfig kAudioConfig) {
            TtsConfig ttsConfig = kAudioConfig.getK().toTtsConfig();
            return buildAudioFormat(ttsConfig);
        }
    },

    ALI("ali") {
        @Override
        public KTtsApi buildAudioApi(KAudioConfig kAudioConfig, String conversationId, AudioCacheService audioCacheService) {
            return new FluxAliTtsImpl(kAudioConfig, conversationId);
        }

        @Override
        public KAudioFormat buildAudioFormat(KAudioConfig kAudioConfig) {
            TtsConfig ttsConfig = kAudioConfig.getAli().toTtsConfig();
            return buildAudioFormat(ttsConfig);
        }
    },

    ALI_NO_STREAM("ali_no_stream") {
        @Override
        public KTtsApi buildAudioApi(KAudioConfig kAudioConfig, String conversationId, AudioCacheService audioCacheService) {
            TtsConfig ttsConfig = kAudioConfig.getAli().toTtsConfig();
            return new KTtsImpl(new AliTtsClient(ttsConfig), ttsConfig, conversationId,
                    false, audioCacheService);
        }

        @Override
        public KAudioFormat buildAudioFormat(KAudioConfig kAudioConfig) {
            TtsConfig ttsConfig = kAudioConfig.getAli().toTtsConfig();
            return buildAudioFormat(ttsConfig);
        }
    },

    QWEN("qwen") {
        @Override
        public KTtsApi buildAudioApi(KAudioConfig kAudioConfig, String conversationId, AudioCacheService audioCacheService) {
            TtsConfig ttsConfig = kAudioConfig.getQwen().toTtsConfig();
            return new KTtsImpl(new QwenServerCommitTtsClient(ttsConfig), ttsConfig, conversationId,
                    false, audioCacheService);
        }

        @Override
        public KAudioFormat buildAudioFormat(KAudioConfig kAudioConfig) {
            TtsConfig ttsConfig = kAudioConfig.getQwen().toTtsConfig();
            return buildAudioFormat(ttsConfig);
        }
    },

    QWEN_COMMIT("qwen_commit") {
        @Override
        public KTtsApi buildAudioApi(KAudioConfig kAudioConfig, String conversationId, AudioCacheService audioCacheService) {
            TtsConfig ttsConfig = kAudioConfig.getQwen().toTtsConfig();
            return new KTtsImpl(new QwenCommitTtsClient(ttsConfig), ttsConfig, conversationId,
                    false, audioCacheService);
        }

        @Override
        public KAudioFormat buildAudioFormat(KAudioConfig kAudioConfig) {
            TtsConfig ttsConfig = kAudioConfig.getQwen().toTtsConfig();
            return buildAudioFormat(ttsConfig);
        }
    },
    ;

    private final String code;


    public abstract KTtsApi buildAudioApi(KAudioConfig kAudioConfig, String conversationId, AudioCacheService audioCacheService);

    public abstract KAudioFormat buildAudioFormat(KAudioConfig kAudioConfig);


    public KAudioFormat buildAudioFormat(TtsConfig ttsConfig) {
        AudioFormat audioFormat = new AudioFormat(ttsConfig.sampleRate(), 16, 1, true, false);
        return new KAudioFormat(ttsConfig.format(), audioFormat);
    }


    public static TtsType getByCode(String code) {
        for (TtsType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
