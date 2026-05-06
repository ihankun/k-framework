package io.hankun.framework.ai.agent.audio.config;

import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: AudioInfo
 * @createAt: 2025/11/25 11:35
 * @author: hankun
 */
public record AudioInfo(String type, String voice, String format, int sampleRate) {

    @NotNull
    @Override
    public String toString() {
        return type + ":" + voice + ":" + format + ":" + sampleRate;
    }


    public static AudioInfo of(String type, String voice, String format, int sampleRate) {
        return new AudioInfo(type, voice, format, sampleRate);
    }

    public static AudioInfo of(String type, TtsConfig ttsConfig) {
        return new AudioInfo(type, ttsConfig.voice(), ttsConfig.format(), ttsConfig.sampleRate());
    }
}
